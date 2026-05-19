package com.octopet.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun OctopetApp() {
    val grid = remember { MockData.generateContributionGrid() }
    var appState by remember {
        mutableStateOf(
            AppState(
                family = PetFamily.OCTO,
                stage = stageForContribs(MockData.stats.totalContributions),
                mood = PetMood.HAPPY,
                stats = MockData.stats,
                onboarded = false,
            )
        )
    }
    var activeTab by remember { mutableStateOf(Tab.HOME) }

    if (!appState.onboarded) {
        OnboardingScreen(onComplete = { appState = appState.copy(onboarded = true) })
        return
    }

    Scaffold(
        containerColor = Paper,
        bottomBar = {
            OctopetTabBar(
                activeTab = activeTab,
                onTabSelected = { activeTab = it },
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
            when (activeTab) {
                Tab.HOME    -> HomeScreen(state = appState, grid = grid)
                Tab.GOALS   -> GoalsScreen(stats = appState.stats)
                Tab.BADGES  -> BadgesScreen()
                Tab.PROFILE -> ProfileScreen(state = appState, grid = grid)
            }
        }
    }
}

@Composable
private fun OctopetTabBar(activeTab: Tab, onTabSelected: (Tab) -> Unit) {
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
                    Text(
                        item.icon,
                        fontSize = 20.sp,
                        color = if (selected) Ink else InkMuted,
                    )
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

private data class TabItem(val tab: Tab, val label: String, val icon: String)
