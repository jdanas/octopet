package com.octopet.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.octopet.app.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class UiState(
    val onboarded: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val appState: AppState = AppState(),
    val user: User? = null,
    val grid: List<List<Int>> = MockData.generateContributionGrid(),
    val activity: List<ActivityItem> = emptyList(),
)

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = GitHubRepository(app)

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init {
        viewModelScope.launch {
            val (username, token) = repo.credentials.first()
            if (username.isNotEmpty() && token.isNotEmpty()) {
                _state.value = _state.value.copy(onboarded = true, isLoading = true)
                refresh(username, token)
            }
        }
    }

    fun onboard(username: String, token: String) {
        viewModelScope.launch {
            repo.saveCredentials(username, token)
            _state.value = _state.value.copy(onboarded = true, isLoading = true)
            refresh(username, token)
        }
    }

    fun retry() {
        viewModelScope.launch {
            val (username, token) = repo.credentials.first()
            if (username.isNotEmpty() && token.isNotEmpty()) {
                _state.value = _state.value.copy(isLoading = true, error = null)
                refresh(username, token)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repo.clearCredentials()
            _state.value = UiState()
        }
    }

    private suspend fun refresh(username: String, token: String) {
        when (val result = fetchGitHubData(username, token)) {
            is ApiResult.Success -> {
                val data = result.data
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
                val stage = stageForContribs(data.totalContributions)
                val mood  = moodForData(data)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = null,
                    user = User(data.username, data.displayName, data.joinedAt),
                    grid = data.grid,
                    activity = data.recentActivity,
                    appState = AppState(
                        family = PetFamily.OCTO,
                        stage  = stage,
                        mood   = mood,
                        stats  = stats,
                        onboarded = true,
                    ),
                )
            }
            is ApiResult.Error -> {
                _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }
    }
}

private fun moodForData(data: GitHubData): PetMood = when {
    data.todayCount >= 6                                      -> PetMood.EXCITED
    data.todayCount > 0                                       -> PetMood.HAPPY
    data.currentStreak > 0                                    -> PetMood.NEUTRAL
    data.thisWeekCount == 0 && data.currentStreak == 0        -> PetMood.SLEEPING
    else                                                      -> PetMood.HUNGRY
}
