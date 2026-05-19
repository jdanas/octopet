package com.octopet.app.ui.creature

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.octopet.app.data.PetMood
import com.octopet.app.data.PetStage
import com.octopet.app.ui.theme.*

// All drawing uses a 200×200 virtual coordinate space (matching the SVG viewBox),
// scaled by `s = canvasSize / 200` at draw time.

@Composable
fun OctoPet(
    stage: PetStage,
    mood: PetMood,
    size: Dp = 180.dp,
    animated: Boolean = true,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wobble")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (animated) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "wobbleY",
    )

    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.width / 200f
        val yOff = if (animated) offsetY * 3f * s else 0f

        withTransform({ translate(left = 0f, top = yOff) }) {
            when (stage) {
                PetStage.EGG       -> drawEgg(s, mood)
                PetStage.SPROUT    -> drawSprout(s, mood)
                PetStage.HATCHLING -> drawHatchling(s, mood)
                PetStage.FLEDGLING -> drawFledgling(s, mood)
                PetStage.ELDER     -> drawElder(s, mood)
            }
        }
    }
}

// ── Eye helpers ──────────────────────────────────────────────

private fun DrawScope.drawEye(cx: Float, cy: Float, r: Float, mood: PetMood, s: Float) {
    when (mood) {
        PetMood.HAPPY -> {
            val path = Path().apply {
                moveTo((cx - r) * s, cy * s)
                quadraticTo(cx * s, (cy - r * 1.2f) * s, (cx + r) * s, cy * s)
            }
            drawPath(path, Ink, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))
        }
        PetMood.SLEEPING -> {
            val path = Path().apply {
                moveTo((cx - r) * s, cy * s)
                quadraticTo(cx * s, (cy + r * 0.8f) * s, (cx + r) * s, cy * s)
            }
            drawPath(path, Ink, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))
        }
        PetMood.HUNGRY -> {
            val path = Path().apply {
                moveTo((cx - r) * s, (cy + r * 0.5f) * s)
                quadraticTo(cx * s, (cy - r * 0.3f) * s, (cx + r) * s, (cy + r * 0.5f) * s)
            }
            drawPath(path, Ink, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))
        }
        PetMood.EXCITED -> {
            drawCircle(Ink, radius = r * s, center = Offset(cx * s, cy * s))
            drawCircle(Color.White, radius = r * 0.5f * s, center = Offset((cx + r * 0.4f) * s, (cy - r * 0.4f) * s))
        }
        else -> {
            drawCircle(Ink, radius = r * s, center = Offset(cx * s, cy * s))
            drawCircle(Color.White, radius = r * 0.4f * s, center = Offset((cx + r * 0.35f) * s, (cy - r * 0.35f) * s))
        }
    }
}

// ── Stage drawers ────────────────────────────────────────────

private fun DrawScope.drawEgg(s: Float, mood: PetMood) {
    drawOval(EggShell, topLeft = Offset(45 * s, 42 * s), size = Size(110 * s, 136 * s))
    val crack1 = Path().apply {
        moveTo(75 * s, 90 * s); lineTo(85 * s, 100 * s); lineTo(80 * s, 110 * s); lineTo(92 * s, 120 * s)
    }
    drawPath(crack1, EggShellDark, style = Stroke(width = 1.5f * s, cap = StrokeCap.Round))
    val crack2 = Path().apply {
        moveTo(120 * s, 85 * s); lineTo(115 * s, 95 * s); lineTo(125 * s, 105 * s)
    }
    drawPath(crack2, EggShellDark, style = Stroke(width = 1.5f * s, cap = StrokeCap.Round))
    drawCircle(EggShellDark.copy(alpha = 0.4f), radius = 4 * s, center = Offset(85 * s, 130 * s))
    drawCircle(EggShellDark.copy(alpha = 0.4f), radius = 3 * s, center = Offset(115 * s, 140 * s))
    drawCircle(EggShellDark.copy(alpha = 0.4f), radius = 2.5f * s, center = Offset(105 * s, 85 * s))
}

private fun DrawScope.drawSprout(s: Float, mood: PetMood) {
    drawOval(Color(0xFF6B5437).copy(alpha = 0.4f), topLeft = Offset(50 * s, 153 * s), size = Size(100 * s, 24 * s))
    drawRoundRect(CreatureBodyDark, topLeft = Offset(96 * s, 120 * s), size = Size(8 * s, 50 * s), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4 * s))
    drawOval(CreatureBody, topLeft = Offset(72 * s, 89 * s), size = Size(56 * s, 52 * s))
    drawOval(CreatureBelly, topLeft = Offset(80 * s, 105 * s), size = Size(40 * s, 30 * s))
    drawOval(CreatureBody, topLeft = Offset(54 * s, 101 * s), size = Size(36 * s, 18 * s))
    drawOval(CreatureBody, topLeft = Offset(110 * s, 101 * s), size = Size(36 * s, 18 * s))
    drawEye(91f, 110f, 3f, mood, s)
    drawEye(109f, 110f, 3f, mood, s)
    if (mood == PetMood.HAPPY) {
        val mouth = Path().apply { moveTo(95 * s, 118 * s); quadraticTo(100 * s, 122 * s, 105 * s, 118 * s) }
        drawPath(mouth, Ink, style = Stroke(width = 1.5f * s, cap = StrokeCap.Round))
    }
}

private fun DrawScope.drawHatchling(s: Float, mood: PetMood) {
    drawOval(Color.Black.copy(alpha = 0.1f), topLeft = Offset(60 * s, 164 * s), size = Size(80 * s, 12 * s))
    val body = Path().apply {
        moveTo(50 * s, 110 * s)
        quadraticTo(50 * s, 60 * s, 100 * s, 60 * s)
        quadraticTo(150 * s, 60 * s, 150 * s, 110 * s)
        quadraticTo(150 * s, 160 * s, 100 * s, 165 * s)
        quadraticTo(50 * s, 160 * s, 50 * s, 110 * s)
        close()
    }
    drawPath(body, CreatureBody)
    drawOval(CreatureBelly, topLeft = Offset(68 * s, 97 * s), size = Size(64 * s, 56 * s))
    listOf(65f, 85f, 115f, 135f).forEach { x ->
        drawOval(CreatureBody, topLeft = Offset((x - 10) * s, 141 * s), size = Size(20 * s, 28 * s))
    }
    drawEye(85f, 100f, 5f, mood, s)
    drawEye(115f, 100f, 5f, mood, s)
    if (mood == PetMood.HAPPY) {
        drawCircle(CreatureCheek.copy(alpha = 0.6f), radius = 5 * s, center = Offset(75 * s, 115 * s))
        drawCircle(CreatureCheek.copy(alpha = 0.6f), radius = 5 * s, center = Offset(125 * s, 115 * s))
        val mouth = Path().apply { moveTo(92 * s, 122 * s); quadraticTo(100 * s, 126 * s, 108 * s, 122 * s) }
        drawPath(mouth, Ink, style = Stroke(width = 2f * s, cap = StrokeCap.Round))
    }
    if (mood == PetMood.NEUTRAL) {
        drawLine(Ink, Offset(94 * s, 120 * s), Offset(106 * s, 120 * s), 2f * s, cap = StrokeCap.Round)
    }
    if (mood == PetMood.HUNGRY) {
        val mouth = Path().apply { moveTo(92 * s, 122 * s); quadraticTo(100 * s, 116 * s, 108 * s, 122 * s) }
        drawPath(mouth, Ink, style = Stroke(width = 2f * s, cap = StrokeCap.Round))
    }
    if (mood == PetMood.SLEEPING) {
        val mouth = Path().apply { moveTo(92 * s, 120 * s); quadraticTo(100 * s, 124 * s, 108 * s, 120 * s) }
        drawPath(mouth, Ink, style = Stroke(width = 2f * s, cap = StrokeCap.Round))
        drawCircle(Ink.copy(alpha = 0.4f), radius = 2.5f * s, center = Offset(150 * s, 75 * s))
        drawCircle(Ink.copy(alpha = 0.4f), radius = 3.5f * s, center = Offset(158 * s, 65 * s))
    }
}

private fun DrawScope.drawFledgling(s: Float, mood: PetMood) {
    drawOval(Color.Black.copy(alpha = 0.1f), topLeft = Offset(55 * s, 169 * s), size = Size(90 * s, 12 * s))
    val body = Path().apply {
        moveTo(40 * s, 105 * s)
        quadraticTo(40 * s, 50 * s, 100 * s, 50 * s)
        quadraticTo(160 * s, 50 * s, 160 * s, 105 * s)
        quadraticTo(160 * s, 165 * s, 100 * s, 170 * s)
        quadraticTo(40 * s, 165 * s, 40 * s, 105 * s)
        close()
    }
    drawPath(body, CreatureBody)
    drawOval(CreatureBelly, topLeft = Offset(62 * s, 93 * s), size = Size(76 * s, 64 * s))
    listOf(55f, 78f, 100f, 122f, 145f).forEach { x ->
        drawOval(CreatureBody, topLeft = Offset((x - 10) * s, 149 * s), size = Size(20 * s, 32 * s))
        drawCircle(CreatureBodyDark, radius = 2 * s, center = Offset(x * s, 172 * s))
    }
    val crown = Path().apply {
        moveTo(85 * s, 55 * s); lineTo(90 * s, 42 * s); lineTo(95 * s, 55 * s); close()
        moveTo(100 * s, 52 * s); lineTo(105 * s, 38 * s); lineTo(110 * s, 52 * s); close()
        moveTo(115 * s, 55 * s); lineTo(110 * s, 42 * s); lineTo(105 * s, 55 * s); close()
    }
    drawPath(crown, CreatureBodyDark)
    drawEye(82f, 98f, 6f, mood, s)
    drawEye(118f, 98f, 6f, mood, s)
    if (mood == PetMood.HAPPY || mood == PetMood.EXCITED) {
        drawCircle(CreatureCheek.copy(alpha = 0.7f), radius = 6 * s, center = Offset(70 * s, 115 * s))
        drawCircle(CreatureCheek.copy(alpha = 0.7f), radius = 6 * s, center = Offset(130 * s, 115 * s))
    }
    when (mood) {
        PetMood.HAPPY -> {
            val m = Path().apply { moveTo(90 * s, 120 * s); quadraticTo(100 * s, 130 * s, 110 * s, 120 * s) }
            drawPath(m, Ink, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))
        }
        PetMood.EXCITED -> drawOval(Ink, topLeft = Offset(94 * s, 117 * s), size = Size(12 * s, 10 * s))
        PetMood.NEUTRAL -> drawLine(Ink, Offset(92 * s, 122 * s), Offset(108 * s, 122 * s), 2.2f * s, cap = StrokeCap.Round)
        PetMood.HUNGRY -> {
            val m = Path().apply { moveTo(90 * s, 125 * s); quadraticTo(100 * s, 118 * s, 110 * s, 125 * s) }
            drawPath(m, Ink, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))
        }
        else -> {}
    }
}

private fun DrawScope.drawElder(s: Float, mood: PetMood) {
    drawOval(Color.Black.copy(alpha = 0.12f), topLeft = Offset(50 * s, 172 * s), size = Size(100 * s, 12 * s))
    val body = Path().apply {
        moveTo(35 * s, 100 * s)
        quadraticTo(35 * s, 42 * s, 100 * s, 42 * s)
        quadraticTo(165 * s, 42 * s, 165 * s, 100 * s)
        quadraticTo(165 * s, 170 * s, 100 * s, 175 * s)
        quadraticTo(35 * s, 170 * s, 35 * s, 100 * s)
        close()
    }
    drawPath(body, CreatureBody)
    drawOval(CreatureBelly, topLeft = Offset(58 * s, 96 * s), size = Size(84 * s, 68 * s))
    listOf(48f, 72f, 100f, 128f, 152f).forEach { x ->
        drawOval(CreatureBody, topLeft = Offset((x - 11) * s, 152 * s), size = Size(22 * s, 36 * s))
        drawCircle(CreatureBodyDark, radius = 2.5f * s, center = Offset(x * s, 178 * s))
    }
    val crown = Path().apply {
        moveTo(75 * s, 48 * s)
        lineTo(80 * s, 30 * s); lineTo(88 * s, 45 * s)
        lineTo(100 * s, 28 * s); lineTo(112 * s, 45 * s)
        lineTo(120 * s, 30 * s); lineTo(125 * s, 48 * s)
        close()
    }
    drawPath(crown, CrownGold)
    drawCircle(CrownGoldLight, radius = 3 * s, center = Offset(100 * s, 35 * s))
    drawCircle(CrownGoldLight, radius = 2 * s, center = Offset(85 * s, 42 * s))
    drawCircle(CrownGoldLight, radius = 2 * s, center = Offset(115 * s, 42 * s))
    drawEye(80f, 100f, 6f, mood, s)
    drawEye(120f, 100f, 6f, mood, s)
    drawCircle(CreatureCheek.copy(alpha = 0.7f), radius = 7 * s, center = Offset(66 * s, 118 * s))
    drawCircle(CreatureCheek.copy(alpha = 0.7f), radius = 7 * s, center = Offset(134 * s, 118 * s))
    val mPath = if (mood == PetMood.HAPPY)
        Path().apply { moveTo(88 * s, 122 * s); quadraticTo(100 * s, 132 * s, 112 * s, 122 * s) }
    else
        Path().apply { moveTo(90 * s, 124 * s); quadraticTo(100 * s, 128 * s, 110 * s, 124 * s) }
    drawPath(mPath, Ink, style = Stroke(width = 2.5f * s, cap = StrokeCap.Round))
}
