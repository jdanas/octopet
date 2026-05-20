package com.octopet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.octopet.app.data.*
import com.octopet.app.ui.components.*
import com.octopet.app.ui.creature.OctoPet
import com.octopet.app.ui.theme.*

@Composable
fun ProfileScreen(
    state: AppState,
    grid: List<List<Int>>,
    user: User = MockData.user,
    onSignOut: () -> Unit = {},
) {
    val stageIdx = PetStage.entries.indexOf(state.stage)
    val earnedCount = MockData.badges.count { it.earned }

    PaperBg {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(bottom = 100.dp),
        ) {
            // Top action bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, PaperLine, CircleShape)
                        .background(Color.White, CircleShape),
                ) {
                    Text("⏏", fontSize = 16.sp, color = Ink)
                }
            }

            // Avatar + name
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OctoPet(
                    stage = state.stage,
                    mood = PetMood.HAPPY,
                    size = 120.dp,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    user.displayName,
                    color = Ink,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                )
                Text(
                    "@${user.username}",
                    color = InkMuted,
                    fontFamily = JetBrainsMono,
                    fontSize = 14.sp,
                )
                Spacer(Modifier.height(12.dp))
                Surface(shape = CircleShape, color = MossSoft) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("✦", fontSize = 12.sp, color = MossDeep)
                        Text(
                            "Stage ${stageIdx + 1} · ${state.stage.label}",
                            color = MossDeep,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.6.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Stats 2×2 grid
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile("Total contributions", "${state.stats.totalContributions}", CardTone.MOSS, Modifier.weight(1f))
                    StatTile("Current streak", "${state.stats.currentStreak}d", CardTone.CORAL, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile("Longest streak", "${state.stats.longestStreak}d", CardTone.BUTTER, Modifier.weight(1f))
                    StatTile("Badges earned", "$earnedCount", CardTone.LAVENDER, Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Breakdown
            OctoCard(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Text("Breakdown", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(14.dp))
                BreakdownRow("●", "Commits", state.stats.commits, MossDeep)
                HorizontalDivider(color = PaperLine, thickness = 1.dp)
                BreakdownRow("⟲", "Pull requests", state.stats.prs, Lavender)
                HorizontalDivider(color = PaperLine, thickness = 1.dp)
                BreakdownRow("◎", "Issues", state.stats.issues, Coral)
            }

            Spacer(Modifier.height(16.dp))

            // Full heatmap
            OctoCard(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("12 months", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        "since ${user.joinedAt}",
                        color = InkMuted,
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                    )
                }
                Spacer(Modifier.height(12.dp))
                Heatmap(grid = grid, cellSize = 6.dp, gap = 2.dp)
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, tone: CardTone, modifier: Modifier = Modifier) {
    OctoCard(modifier = modifier, tone = tone) {
        Text(
            label.uppercase(),
            color = InkSoft,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            value,
            color = Ink,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = JetBrainsMono,
            letterSpacing = (-0.5).sp,
        )
    }
}

@Composable
private fun BreakdownRow(icon: String, label: String, value: Int, iconColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PaperDeep),
            contentAlignment = Alignment.Center,
        ) {
            Text(icon, fontSize = 14.sp, color = iconColor)
        }
        Text(label, color = Ink, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(
            "$value",
            color = Ink,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = JetBrainsMono,
        )
    }
}
