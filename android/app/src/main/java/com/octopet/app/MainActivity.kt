package com.octopet.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.octopet.app.data.*
import com.octopet.app.ui.screens.*
import com.octopet.app.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OctopetTheme {
                OctopetApp()
            }
        }
    }
}

enum class Tab { HOME, GOALS, BADGES, PROFILE }

@Composable
fun OctopetApp(vm: MainViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    when {
        !state.onboarded -> OnboardingScreen(uiState = state, onStartOAuth = vm::startOAuth)
        state.isLoading  -> LoadingScreen()
        state.error != null -> ErrorScreen(message = state.error!!, onRetry = vm::retry)
        else -> MainContent(state = state, onSignOut = vm::signOut)
    }
}

@Composable
private fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator(color = Ink, strokeWidth = 2.dp)
            Text("Hatching your pet…", color = InkMuted, fontSize = 14.sp)
        }
    }
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text("Could not fetch data", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Ink)
            Text(message, fontSize = 13.sp, color = InkMuted)
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = Paper),
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun MainContent(state: UiState, onSignOut: () -> Unit) {
    var activeTab by remember { mutableStateOf(Tab.HOME) }

    Scaffold(
        containerColor = Paper,
        bottomBar = {
            OctopetTabBar(activeTab = activeTab, onTabSelected = { activeTab = it })
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
            when (activeTab) {
                Tab.HOME    -> HomeScreen(
                    state = state.appState,
                    grid = state.grid,
                    user = state.user ?: com.octopet.app.data.MockData.user,
                    activity = state.activity.ifEmpty { com.octopet.app.data.MockData.activity },
                )
                Tab.GOALS   -> GoalsScreen(stats = state.appState.stats)
                Tab.BADGES  -> BadgesScreen()
                Tab.PROFILE -> ProfileScreen(
                    state = state.appState,
                    grid = state.grid,
                    user = state.user ?: com.octopet.app.data.MockData.user,
                    onSignOut = onSignOut,
                )
            }
        }
    }
}

@Composable
private fun OctopetTabBar(activeTab: Tab, onTabSelected: (Tab) -> Unit) {
    data class TabItem(val tab: Tab, val label: String, val icon: String)
    val tabs = listOf(
        TabItem(Tab.HOME,    "Home",    "⌂"),
        TabItem(Tab.GOALS,   "Goals",   "◎"),
        TabItem(Tab.BADGES,  "Badges",  "✦"),
        TabItem(Tab.PROFILE, "Profile", "○"),
    )

    NavigationBar(
        containerColor = Paper.copy(alpha = 0.94f),
        tonalElevation = 0.dp,
    ) {
        tabs.forEach { item ->
            val selected = activeTab == item.tab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Text(item.icon, fontSize = 20.sp, color = if (selected) Ink else InkMuted)
                },
                label = {
                    Text(
                        item.label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (selected) Ink else InkMuted,
                        letterSpacing = 0.2.sp,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Ink,
                    indicatorColor = MossSoft,
                ),
            )
        }
    }
}
