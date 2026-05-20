package com.octopet.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.octopet.app.data.Goal
import com.octopet.app.data.MockData
import com.octopet.app.data.Stats
import com.octopet.app.ui.components.*
import com.octopet.app.ui.theme.*

@Composable
fun GoalsScreen(
    stats: Stats = MockData.stats,
) {
    val goals = listOf(
        Goal("weekly_commits", "Weekly commits",  target = 40,  current = stats.thisWeek,     unit = "commits", period = "this week"),
        Goal("streak_goal",    "Streak goal",     target = 30,  current = stats.currentStreak,unit = "days",    period = "current"),
        Goal("monthly_prs",    "Pull requests",   target = 8,   current = stats.prs.coerceAtMost(8),  unit = "PRs",     period = "this month"),
        Goal("monthly_issues", "Issues opened",   target = 10,  current = stats.issues.coerceAtMost(10), unit = "issues", period = "this month"),
    )
    PaperBg {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(bottom = 100.dp),
        ) {
            // Header
            Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 20.dp)) {
                Text(
                    "Your targets",
                    color = InkMuted,
                    fontFamily = JetBrainsMono,
                    fontSize = 13.sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Goals",
                    color = Ink,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.8).sp,
                )
            }

            // Weekly ring hero
            OctoCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                tone = CardTone.BUTTER,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    RingProgress(
                        value = stats.thisWeek / 40f,
                        size = 96.dp,
                        strokeWidth = 10.dp,
                        color = Butter,
                        track = Butter.copy(alpha = 0.3f),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${stats.thisWeek}",
                                color = Ink,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMono,
                                lineHeight = 22.sp,
                            )
                            Text(
                                "of 40",
                                color = InkMuted,
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                            )
                        }
                    }
                    Column {
                        Text("This week", color = InkSoft, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Spacer(Modifier.height(2.dp))
                        val weeklyTarget = 40
                        val weeklyHeadline = when {
                            stats.thisWeek >= weeklyTarget -> "Goal crushed!"
                            stats.thisWeek >= weeklyTarget * 0.8 -> "Nearly there"
                            stats.thisWeek == 0 -> "Let's go!"
                            else -> "Keep going"
                        }
                        Text(
                            weeklyHeadline,
                            color = Ink,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp,
                        )
                        Spacer(Modifier.height(4.dp))
                        val remaining = (weeklyTarget - stats.thisWeek).coerceAtLeast(0)
                        Text(
                            if (remaining == 0) "You hit your weekly target!"
                            else "$remaining more commits to hit your goal.",
                            color = InkMuted,
                            fontSize = 13.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Goal cards
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                goals.forEach { goal ->
                    GoalCard(goal)
                }

                // Add goal button
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(PaperLine),
                    ),
                ) {
                    Text("+ Add a goal", color = InkMuted, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun GoalCard(goal: Goal) {
    val pct = (goal.current.toFloat() / goal.target).coerceIn(0f, 1f)
    val done = goal.current >= goal.target

    OctoCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(goal.name, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(goal.period, color = InkMuted, fontFamily = JetBrainsMono, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "${goal.current}",
                        color = Ink,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JetBrainsMono,
                    )
                    Text(
                        "/${goal.target}",
                        color = InkFaint,
                        fontSize = 18.sp,
                        fontFamily = JetBrainsMono,
                    )
                }
                Text(goal.unit, color = InkMuted, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(10.dp))
        OctoProgressBar(
            value = pct,
            color = if (done) Moss else Ink,
        )
    }
}
