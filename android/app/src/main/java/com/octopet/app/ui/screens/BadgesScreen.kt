package com.octopet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.octopet.app.data.Badge
import com.octopet.app.data.BadgeIcon
import com.octopet.app.data.MockData
import com.octopet.app.ui.components.OctoProgressBar
import com.octopet.app.ui.components.SectionLabel
import com.octopet.app.ui.theme.*

@Composable
fun BadgesScreen(badges: List<Badge> = MockData.badges) {
    val earned = badges.filter { it.earned }
    val locked = badges.filter { !it.earned }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .systemBarsPadding()
            .padding(bottom = 100.dp),
    ) {
        // Header
        Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp)) {
            Text(
                "${earned.size} of ${badges.size} earned",
                color = InkMuted,
                fontFamily = JetBrainsMono,
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Achievements",
                color = Ink,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.8).sp,
            )
            Spacer(Modifier.height(16.dp))
            // gradient progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(PaperLine),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(earned.size.toFloat() / badges.size)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Brush.horizontalGradient(listOf(Moss, Lavender))),
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                SectionLabel("Earned · ${earned.size}")
            }
            items(earned) { BadgeCard(it, earned = true) }

            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                SectionLabel("Locked · ${locked.size}", modifier = Modifier.padding(top = 14.dp))
            }
            items(locked) { BadgeCard(it, earned = false) }
        }
    }
}

@Composable
private fun BadgeCard(badge: Badge, earned: Boolean) {
    val iconBg = if (earned) LavenderSoft else PaperDeep
    val iconColor = if (earned) Lavender else InkFaint

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (earned) Color.White else PaperDeep)
            .border(1.dp, PaperLine, RoundedCornerShape(18.dp))
            .then(if (!earned) Modifier else Modifier)
            .padding(14.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = badge.icon.toEmoji(),
                    fontSize = 22.sp,
                    color = iconColor,
                )
                if (!earned) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(4.dp, (-4).dp)
                            .size(18.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(PaperDeep)
                            .border(1.dp, PaperLine, RoundedCornerShape(9.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("🔒", fontSize = 8.sp)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                badge.name,
                color = if (earned) Ink else InkMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                badge.desc,
                color = InkMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp,
            )
            if (earned && badge.date != null) {
                Spacer(Modifier.height(6.dp))
                Text(badge.date, color = InkMuted, fontFamily = JetBrainsMono, fontSize = 10.sp)
            }
        }
        if (!earned) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.35f))
            )
        }
    }
}

private fun BadgeIcon.toEmoji() = when (this) {
    BadgeIcon.SEED      -> "🌱"
    BadgeIcon.FLAME     -> "🔥"
    BadgeIcon.FLAME2    -> "💥"
    BadgeIcon.TROPHY    -> "🏆"
    BadgeIcon.MERGE     -> "⟲"
    BadgeIcon.MOON      -> "🌙"
    BadgeIcon.GLOBE     -> "🌐"
    BadgeIcon.LIGHTNING -> "⚡"
    BadgeIcon.SUN       -> "☀️"
    BadgeIcon.CROWN     -> "👑"
    BadgeIcon.HEART     -> "❤️"
    BadgeIcon.EYE       -> "👁"
}
