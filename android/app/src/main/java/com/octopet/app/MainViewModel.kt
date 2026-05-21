package com.octopet.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.octopet.app.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

enum class OAuthStep { IDLE, REQUESTING, AWAITING_AUTH, FETCHING_DATA }

data class UiState(
    val onboarded: Boolean        = false,
    val isLoading: Boolean        = false,
    val error: String?            = null,
    val appState: AppState        = AppState(),
    val user: User?               = null,
    val grid: List<List<Int>>     = MockData.generateContributionGrid(),
    val activity: List<ActivityItem> = emptyList(),
    // OAuth device flow
    val oauthStep: OAuthStep      = OAuthStep.IDLE,
    val userCode: String          = "",
    val verificationUri: String   = "https://github.com/login/device",
)

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = GitHubRepository(app)

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init {
        // Auto-login if token already saved
        viewModelScope.launch {
            val (_, token) = repo.credentials.first()
            if (token.isNotEmpty()) {
                val profile = ensurePetProfile()
                _state.value = _state.value.copy(onboarded = true, isLoading = true)
                refresh(token, profile)
            }
        }
    }

    // ── OAuth Device Flow ────────────────────────────────────────────────────

    fun startOAuth() {
        viewModelScope.launch {
            _state.value = _state.value.copy(oauthStep = OAuthStep.REQUESTING, error = null)
            when (val result = requestDeviceCode()) {
                is ApiResult.Error   -> _state.value = _state.value.copy(
                    oauthStep = OAuthStep.IDLE,
                    error = result.message,
                )
                is ApiResult.Success -> {
                    val code = result.data
                    _state.value = _state.value.copy(
                        oauthStep       = OAuthStep.AWAITING_AUTH,
                        userCode        = code.userCode,
                        verificationUri = code.verificationUri,
                    )
                    pollForToken(code.deviceCode, code.interval).collect { poll ->
                        when (poll) {
                            is AuthPollResult.Pending -> { /* keep waiting */ }
                            is AuthPollResult.Success -> {
                                _state.value = _state.value.copy(
                                    oauthStep = OAuthStep.FETCHING_DATA,
                                    onboarded = true,
                                    isLoading = true,
                                )
                                repo.saveCredentials("", poll.token)
                                val profile = ensurePetProfile()
                                refresh(poll.token, profile)
                            }
                            is AuthPollResult.Expired -> _state.value = _state.value.copy(
                                oauthStep = OAuthStep.IDLE,
                                error = "Code expired — please try again.",
                            )
                            is AuthPollResult.Error   -> _state.value = _state.value.copy(
                                oauthStep = OAuthStep.IDLE,
                                error = poll.message,
                            )
                        }
                    }
                }
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            val (_, token) = repo.credentials.first()
            if (token.isNotEmpty()) {
                val profile = ensurePetProfile()
                _state.value = _state.value.copy(isLoading = true, error = null)
                refresh(token, profile)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repo.clearCredentials()
            _state.value = UiState()
        }
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    // Load the persisted PetProfile, or create+persist a fresh one (mystery-box roll).
    private suspend fun ensurePetProfile(): PetProfile {
        repo.petProfile.first()?.let { return it }
        val fresh = PetProfile(
            signupDate  = LocalDate.now(),
            variantSeed = Random.nextLong(),
        )
        repo.savePetProfile(fresh)
        Log.d("OctoPet", "rolled mystery box: seed=${fresh.variantSeed} signup=${fresh.signupDate}")
        return fresh
    }

    private suspend fun refresh(token: String, profile: PetProfile) {
        when (val result = fetchGitHubData(token, profile.signupDate)) {
            is ApiResult.Error   -> _state.value = _state.value.copy(
                isLoading = false,
                error     = result.message,
                oauthStep = OAuthStep.IDLE,
            )
            is ApiResult.Success -> {
                val data  = result.data
                val stats = Stats(
                    totalContributions = data.totalContributions,
                    currentStreak      = data.currentStreak,
                    longestStreak      = data.longestStreak,
                    thisWeek           = data.thisWeekCount,
                    thisMonth          = data.thisMonthCount,
                    commits            = data.commits,
                    prs                = data.prs,
                    issues             = data.issues,
                )
                // Persist the resolved username alongside the token
                repo.saveCredentials(data.username, token)

                _state.value = _state.value.copy(
                    isLoading = false,
                    error     = null,
                    oauthStep = OAuthStep.IDLE,
                    user      = User(data.username, data.displayName, data.joinedAt),
                    grid      = data.grid,
                    activity  = data.recentActivity,
                    appState  = AppState(
                        family           = PetFamily.OCTO,
                        stage            = stageForContribs(data.petContributions),
                        mood             = moodForData(data),
                        stats            = stats,
                        onboarded        = true,
                        petContributions = data.petContributions,
                        variantSeed      = profile.variantSeed,
                    ),
                )
            }
        }
    }
}

private fun moodForData(data: GitHubData): PetMood = when {
    data.todayCount >= 6                               -> PetMood.EXCITED
    data.todayCount > 0                                -> PetMood.HAPPY
    data.currentStreak > 0                             -> PetMood.NEUTRAL
    data.thisWeekCount == 0 && data.currentStreak == 0 -> PetMood.SLEEPING
    else                                               -> PetMood.HUNGRY
}
