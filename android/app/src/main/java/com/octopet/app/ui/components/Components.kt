package com.octopet.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.octopet.app.ui.theme.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import com.octopet.app.data.MockData
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ── Paper background ─────────────────────────────────────────

@Composable
fun PaperBg(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Paper)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0x10C8A050), Color.Transparent),
                    center = Offset(0.2f, 0.1f),
                    radius = 800f,
                )
            ),
        content = content,
    )
}

// ── Octo card ────────────────────────────────────────────────

enum class CardTone { DEFAULT, MOSS, CORAL, LAVENDER, BUTTER, DEEP }

@Composable
fun OctoCard(
    modifier: Modifier = Modifier,
    tone: CardTone = CardTone.DEFAULT,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val bg = when (tone) {
        CardTone.MOSS     -> MossSoft
        CardTone.CORAL    -> CoralSoft
        CardTone.LAVENDER -> LavenderSoft
        CardTone.BUTTER   -> ButterSoft
        CardTone.DEEP     -> PaperDeep
        CardTone.DEFAULT  -> Color.White
    }
    val cardShape = RoundedCornerShape(20.dp)
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = cardShape,
            colors = CardDefaults.cardColors(containerColor = bg),
            border = androidx.compose.foundation.BorderStroke(1.dp, PaperLine),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) { Column(modifier = Modifier.padding(16.dp), content = content) }
    } else {
        Card(
            modifier = modifier,
            shape = cardShape,
            colors = CardDefaults.cardColors(containerColor = bg),
            border = androidx.compose.foundation.BorderStroke(1.dp, PaperLine),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) { Column(modifier = Modifier.padding(16.dp), content = content) }
    }
}

// ── Progress ring (Canvas) ───────────────────────────────────

@Composable
fun RingProgress(
    value: Float,           // 0..1
    size: Dp = 96.dp,
    strokeWidth: Dp = 10.dp,
    color: Color = Moss,
    track: Color = PaperLine,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sw = strokeWidth.toPx()
            val r = (this.size.width - sw) / 2f
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            // track
            drawCircle(track, radius = r, center = Offset(cx, cy), style = Stroke(sw))
            // filled arc
            val sweep = (value.coerceIn(0f, 1f) * 360f)
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(cx - r, cy - r),
                size = Size(r * 2, r * 2),
                style = Stroke(sw, cap = StrokeCap.Round),
            )
        }
        content()
    }
}

// ── Contribution heatmap ─────────────────────────────────────

@Composable
fun Heatmap(
    grid: List<List<Int>>,
    cellSize: Dp = 10.dp,
    gap: Dp = 2.5.dp,
    modifier: Modifier = Modifier,
) {
    val levels = listOf(Heat0, Heat1, Heat2, Heat3, Heat4)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        grid.forEach { week ->
            Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                week.forEach { count ->
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .clip(RoundedCornerShape(2.dp))
                            .background(levels[MockData.contribLevel(count)])
                    )
                }
            }
        }
    }
}

// ── Stat badge (pill) ────────────────────────────────────────

@Composable
fun StageBadge(stage: String, stageIndex: Int) {
    Surface(
        shape = CircleShape,
        color = Ink,
    ) {
        Text(
            text = "Stage ${stageIndex + 1} · $stage",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            color = Paper,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
        )
    }
}

// ── Section label ────────────────────────────────────────────

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        color = InkMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = JetBrainsMono,
        letterSpacing = 0.6.sp,
    )
}

// ── Progress bar ─────────────────────────────────────────────

@Composable
fun OctoProgressBar(
    value: Float,
    modifier: Modifier = Modifier,
    color: Color = Moss,
    track: Color = PaperLine,
    height: Dp = 8.dp,
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(track)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(value.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(height / 2))
                .background(color)
        )
    }
}
