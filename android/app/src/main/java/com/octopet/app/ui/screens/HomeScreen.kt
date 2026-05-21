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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.octopet.app.data.*
import com.octopet.app.ui.components.*
import com.octopet.app.ui.creature.OctoPet
import com.octopet.app.ui.theme.*

@Composable
fun HomeScreen(
    state: AppState,
    grid: List<List<Int>>,
    user: User = MockData.user,
    activity: List<ActivityItem> = MockData.activity,
) {
    val stageIdx = PetStage.entries.indexOf(state.stage)
    val nextStage = PetStage.entries.getOrNull(stageIdx + 1)
    // Progress toward the next stage is measured against post-signup contributions —
    // historical commits don't pre-hatch the egg.
    val progress = if (nextStage != null) {
        ((state.petContributions - state.stage.minContribs).toFloat() /
                (nextStage.minContribs - state.stage.minContribs))
            .coerceIn(0f, 1f)
    } else 1f

    val speechMessage = mapOf(
        PetMood.HAPPY    to "Just shipped some commits. I feel great!",
        PetMood.NEUTRAL  to "Hey — show me some code today?",
        PetMood.HUNGRY   to "I'm getting hungry... feed me commits!",
        PetMood.SLEEPING to "zzz... code a bit, I'll wake up.",
        PetMood.EXCITED  to "LET'S GO! You're on fire today!",
    )[state.mood] ?: ""

    val recentGrid = grid.takeLast(20)

    PaperBg {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(bottom = 100.dp),
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "@${user.username}",
                        color = InkMuted,
                        fontFamily = JetBrainsMono,
                        fontSize = 13.sp,
                    )
                    Text(
                        text = "Hey, ${user.displayName.split(" ").first()}",
                        color = Ink,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                    )
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, PaperLine, CircleShape)
                        .background(Color.White, CircleShape),
                ) {
                    Text("🔔", fontSize = 16.sp)
                }
            }

            // Pet hero card
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.verticalGradient(listOf(MossSoft, Paper))
                    )
                    .border(1.dp, PaperLine, RoundedCornerShape(28.dp))
                    .padding(20.dp),
            ) {
                Column {
                    StageBadge(stage = state.stage.label, stageIndex = stageIdx)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        OctoPet(
                            stage = state.stage,
                            mood = state.mood,
                            size = 180.dp,
                            variantSeed = state.variantSeed,
                        )
                    }

                    // Speech bubble
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, PaperLine, RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "\"$speechMessage\"",
                            color = Ink,
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }

                    // Evolution progress
                    if (nextStage != null) {
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "${state.petContributions} / ${nextStage.minContribs}",
                                color = InkMuted,
                                fontFamily = JetBrainsMono,
                                fontSize = 12.sp,
                            )
                            Text(
                                text = "→ ${nextStage.label}",
                                color = InkMuted,
                                fontFamily = JetBrainsMono,
                                fontSize = 12.sp,
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        OctoProgressBar(value = progress, color = Moss)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Stat row
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OctoCard(
                    modifier = Modifier.weight(1f),
                    tone = CardTone.CORAL,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔥", fontSize = 16.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Streak",
                            color = InkSoft,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.4.sp,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${state.stats.currentStreak}",
                            color = Ink,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMono,
                        )
                        Text(
                            text = " days",
                            color = InkMuted,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 3.dp),
                        )
                    }
                }
                OctoCard(
                    modifier = Modifier.weight(1f),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⬤", fontSize = 10.sp, color = MossDeep)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "This week",
                            color = InkSoft,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.4.sp,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "${state.stats.thisWeek}",
                        color = Ink,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JetBrainsMono,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Heatmap card
            OctoCard(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Last 20 weeks", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${state.stats.totalContributions} total",
                        color = InkMuted,
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                    )
                }
                Spacer(Modifier.height(12.dp))
                Heatmap(grid = recentGrid, cellSize = 11.dp, gap = 3.dp)
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("less", color = InkMuted, fontSize = 10.sp, fontFamily = JetBrainsMono)
                    Spacer(Modifier.width(4.dp))
                    listOf(Heat0, Heat1, Heat2, Heat3, Heat4).forEach { c ->
                        Spacer(Modifier.width(2.dp))
                        Box(Modifier.size(9.dp).clip(RoundedCornerShape(2.dp)).background(c))
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("more", color = InkMuted, fontSize = 10.sp, fontFamily = JetBrainsMono)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Activity feed
            OctoCard(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Text(
                    "Today's activity",
                    color = Ink,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(10.dp))
                activity.take(3).forEachIndexed { i, item ->
                    if (i > 0) HorizontalDivider(color = PaperLine, thickness = 1.dp)
                    ActivityRow(item)
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(item: ActivityItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val (bg, emoji) = when (item.kind) {
            ActivityKind.COMMIT -> MossSoft to "●"
            ActivityKind.PR     -> LavenderSoft to "⟲"
            ActivityKind.ISSUE  -> CoralSoft to "◎"
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Text(emoji, fontSize = 14.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.message,
                color = Ink,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
            Text(
                "${item.repo} · ${item.timeAgo} ago",
                color = InkMuted,
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
            )
        }
    }
}
