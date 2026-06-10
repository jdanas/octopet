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
import kotlin.math.sin

// All drawing uses a 200×200 virtual coordinate space (matching the SVG viewBox),
// scaled by `s = canvasSize / 200` at draw time.

// ── Mystery-box palettes ─────────────────────────────────────
// One palette per "species" the egg can hatch into. Picked deterministically
// from the user's variantSeed at sign-up. The egg itself is identical across
// palettes — the surprise is revealed only when it hatches.

data class OctoVariant(
    val body: Color,
    val bodyDark: Color,
    val belly: Color,
    val cheek: Color,
)

private val PALETTES = listOf(
    // Moss (original)
    OctoVariant(Color(0xFF6B9E4F), Color(0xFF4A7A36), Color(0xFFC8E0A8), Color(0xFFF5A9A0)),
    // Coral
    OctoVariant(Color(0xFFD96B4A), Color(0xFFA34B30), Color(0xFFFAE3DA), Color(0xFFFFC9A8)),
    // Lavender
    OctoVariant(Color(0xFF9278D4), Color(0xFF6B539E), Color(0xFFEDE7FA), Color(0xFFF5A9A0)),
    // Sky
    OctoVariant(Color(0xFF5A8FB8), Color(0xFF3F6C8E), Color(0xFFD4E8F3), Color(0xFFF5A9A0)),
    // Rose
    OctoVariant(Color(0xFFD86B8E), Color(0xFFA34B68), Color(0xFFFAD9E3), Color(0xFFFAC4D2)),
    // Sand
    OctoVariant(Color(0xFFC9A050), Color(0xFF8E6F2E), Color(0xFFFAF1D6), Color(0xFFF5A9A0)),
    // Mint
    OctoVariant(Color(0xFF52B79A), Color(0xFF347D67), Color(0xFFD2EFE3), Color(0xFFF5A9A0)),
    // Slate
    OctoVariant(Color(0xFF6E7A8A), Color(0xFF4A5563), Color(0xFFDDE3EB), Color(0xFFF5A9A0)),
)

private val DefaultVariant = PALETTES[0]

fun variantFor(seed: Long): OctoVariant {
    if (seed == 0L) return DefaultVariant
    return PALETTES[kotlin.random.Random(seed).nextInt(PALETTES.size)]
}

@Composable
fun OctoPet(
    stage: PetStage,
    mood: PetMood,
    size: Dp = 180.dp,
    animated: Boolean = true,
    variantSeed: Long = 0L,
) {
    val variant = remember(variantSeed) { variantFor(variantSeed) }
    val infiniteTransition = rememberInfiniteTransition(label = "octopet")

    // Mood tweaks the cadence so the pet visibly reflects how it feels.
    val wobbleDuration = when (mood) {
        PetMood.EXCITED -> 700
        PetMood.SLEEPING -> 2600
        else -> 1500
    }
    val breatheDuration = if (mood == PetMood.SLEEPING) 3400 else 2400
    val wiggleDuration = when (mood) {
        PetMood.EXCITED -> 1300
        PetMood.SLEEPING -> 3200
        else -> 2200
    }

    val wobbleY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(wobbleDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "wobbleY",
    )

    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.985f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(breatheDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathe",
    )

    val wigglePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(wiggleDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wigglePhase",
    )

    // Blink: closed for a small window inside a ~4.2s loop.
    val blink by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4200
                0f at 0
                0f at 3900
                1f at 4020
                0f at 4180
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "blink",
    )

    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.width / 200f
        val yOff = if (animated) wobbleY * 3f * s else 0f
        val breatheScale = if (animated) breathe else 1f
        val phase = if (animated) wigglePhase else 0f
        val blinkAmt = if (animated) blink else 0f

        withTransform({
            translate(left = 0f, top = yOff)
            // Anchor breathing at the base so the head expands upward.
            scale(scaleX = 1f, scaleY = breatheScale, pivot = Offset(100f * s, 170f * s))
        }) {
            when (stage) {
                PetStage.EGG       -> drawEgg(s, mood)
                PetStage.SPROUT    -> drawSprout(s, mood, variant, phase, blinkAmt)
                PetStage.HATCHLING -> drawHatchling(s, mood, variant, phase, blinkAmt)
                PetStage.FLEDGLING -> drawFledgling(s, mood, variant, phase, blinkAmt)
                PetStage.ELDER     -> drawElder(s, mood, variant, phase, blinkAmt)
            }
        }
    }
}

// ── Eye helpers ──────────────────────────────────────────────

private fun DrawScope.drawEye(
    cx: Float,
    cy: Float,
    r: Float,
    mood: PetMood,
    s: Float,
    blink: Float = 0f,
) {
    // Blink overlays a closed-eye line for moods whose default eye is round.
    // HAPPY and SLEEPING already use curved/closed eyes, so we leave them alone.
    val blinkable = mood == PetMood.NEUTRAL || mood == PetMood.HUNGRY || mood == PetMood.EXCITED
    if (blinkable && blink > 0.55f) {
        val path = Path().apply {
            moveTo((cx - r) * s, cy * s)
            quadraticTo(cx * s, (cy + r * 0.3f) * s, (cx + r) * s, cy * s)
        }
        drawPath(path, Ink, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))
        return
    }
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

private fun DrawScope.drawSprout(s: Float, mood: PetMood, v: OctoVariant, phase: Float, blink: Float) {
    drawOval(Color(0xFF6B5437).copy(alpha = 0.4f), topLeft = Offset(50 * s, 153 * s), size = Size(100 * s, 24 * s))
    // Stem leans gently with the wiggle.
    val stemSway = sin(phase) * 1.2f
    drawRoundRect(
        v.bodyDark,
        topLeft = Offset((96 + stemSway) * s, 120 * s),
        size = Size(8 * s, 50 * s),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4 * s),
    )
    val headDx = sin(phase) * 1.8f
    drawOval(v.body, topLeft = Offset((72 + headDx) * s, 89 * s), size = Size(56 * s, 52 * s))
    drawOval(v.belly, topLeft = Offset((80 + headDx) * s, 105 * s), size = Size(40 * s, 30 * s))
    // Side leaves flutter slightly out of phase.
    val leafL = sin(phase + 0.6f) * 1.5f
    val leafR = sin(phase + 0.6f + Math.PI.toFloat()) * 1.5f
    drawOval(v.body, topLeft = Offset(54 * s, (101 + leafL) * s), size = Size(36 * s, 18 * s))
    drawOval(v.body, topLeft = Offset(110 * s, (101 + leafR) * s), size = Size(36 * s, 18 * s))
    drawEye(91f + headDx, 110f, 3f, mood, s, blink)
    drawEye(109f + headDx, 110f, 3f, mood, s, blink)
    if (mood == PetMood.HAPPY) {
        val mouth = Path().apply {
            moveTo((95 + headDx) * s, 118 * s)
            quadraticTo((100 + headDx) * s, 122 * s, (105 + headDx) * s, 118 * s)
        }
        drawPath(mouth, Ink, style = Stroke(width = 1.5f * s, cap = StrokeCap.Round))
    }
}

private fun DrawScope.drawHatchling(s: Float, mood: PetMood, v: OctoVariant, phase: Float, blink: Float) {
    drawOval(Color.Black.copy(alpha = 0.1f), topLeft = Offset(60 * s, 164 * s), size = Size(80 * s, 12 * s))
    val body = Path().apply {
        moveTo(50 * s, 110 * s)
        quadraticTo(50 * s, 60 * s, 100 * s, 60 * s)
        quadraticTo(150 * s, 60 * s, 150 * s, 110 * s)
        quadraticTo(150 * s, 160 * s, 100 * s, 165 * s)
        quadraticTo(50 * s, 160 * s, 50 * s, 110 * s)
        close()
    }
    drawPath(body, v.body)
    drawOval(v.belly, topLeft = Offset(68 * s, 97 * s), size = Size(64 * s, 56 * s))
    listOf(65f, 85f, 115f, 135f).forEachIndexed { i, x ->
        val dy = sin(phase + i * 0.9f) * 2.5f
        val dx = sin(phase * 0.5f + i * 0.4f) * 1.2f
        drawOval(v.body, topLeft = Offset((x - 10 + dx) * s, (141 + dy) * s), size = Size(20 * s, 28 * s))
    }
    drawEye(85f, 100f, 5f, mood, s, blink)
    drawEye(115f, 100f, 5f, mood, s, blink)
    if (mood == PetMood.HAPPY) {
        drawCircle(v.cheek.copy(alpha = 0.6f), radius = 5 * s, center = Offset(75 * s, 115 * s))
        drawCircle(v.cheek.copy(alpha = 0.6f), radius = 5 * s, center = Offset(125 * s, 115 * s))
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
        // Drifting zzz's: bob with the wiggle phase.
        val zDrift = sin(phase) * 2f
        drawCircle(Ink.copy(alpha = 0.4f), radius = 2.5f * s, center = Offset(150 * s, (75 + zDrift) * s))
        drawCircle(Ink.copy(alpha = 0.4f), radius = 3.5f * s, center = Offset(158 * s, (65 - zDrift) * s))
    }
}

private fun DrawScope.drawFledgling(s: Float, mood: PetMood, v: OctoVariant, phase: Float, blink: Float) {
    drawOval(Color.Black.copy(alpha = 0.1f), topLeft = Offset(55 * s, 169 * s), size = Size(90 * s, 12 * s))
    val body = Path().apply {
        moveTo(40 * s, 105 * s)
        quadraticTo(40 * s, 50 * s, 100 * s, 50 * s)
        quadraticTo(160 * s, 50 * s, 160 * s, 105 * s)
        quadraticTo(160 * s, 165 * s, 100 * s, 170 * s)
        quadraticTo(40 * s, 165 * s, 40 * s, 105 * s)
        close()
    }
    drawPath(body, v.body)
    drawOval(v.belly, topLeft = Offset(62 * s, 93 * s), size = Size(76 * s, 64 * s))
    listOf(55f, 78f, 100f, 122f, 145f).forEachIndexed { i, x ->
        val dy = sin(phase + i * 0.8f) * 2.8f
        val dx = sin(phase * 0.5f + i * 0.5f) * 1.4f
        drawOval(v.body, topLeft = Offset((x - 10 + dx) * s, (149 + dy) * s), size = Size(20 * s, 32 * s))
        drawCircle(v.bodyDark, radius = 2 * s, center = Offset((x + dx) * s, (172 + dy) * s))
    }
    val crown = Path().apply {
        moveTo(85 * s, 55 * s); lineTo(90 * s, 42 * s); lineTo(95 * s, 55 * s); close()
        moveTo(100 * s, 52 * s); lineTo(105 * s, 38 * s); lineTo(110 * s, 52 * s); close()
        moveTo(115 * s, 55 * s); lineTo(110 * s, 42 * s); lineTo(105 * s, 55 * s); close()
    }
    drawPath(crown, v.bodyDark)
    drawEye(82f, 98f, 6f, mood, s, blink)
    drawEye(118f, 98f, 6f, mood, s, blink)
    if (mood == PetMood.HAPPY || mood == PetMood.EXCITED) {
        drawCircle(v.cheek.copy(alpha = 0.7f), radius = 6 * s, center = Offset(70 * s, 115 * s))
        drawCircle(v.cheek.copy(alpha = 0.7f), radius = 6 * s, center = Offset(130 * s, 115 * s))
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

private fun DrawScope.drawElder(s: Float, mood: PetMood, v: OctoVariant, phase: Float, blink: Float) {
    drawOval(Color.Black.copy(alpha = 0.12f), topLeft = Offset(50 * s, 172 * s), size = Size(100 * s, 12 * s))
    val body = Path().apply {
        moveTo(35 * s, 100 * s)
        quadraticTo(35 * s, 42 * s, 100 * s, 42 * s)
        quadraticTo(165 * s, 42 * s, 165 * s, 100 * s)
        quadraticTo(165 * s, 170 * s, 100 * s, 175 * s)
        quadraticTo(35 * s, 170 * s, 35 * s, 100 * s)
        close()
    }
    drawPath(body, v.body)
    drawOval(v.belly, topLeft = Offset(58 * s, 96 * s), size = Size(84 * s, 68 * s))
    listOf(48f, 72f, 100f, 128f, 152f).forEachIndexed { i, x ->
        val dy = sin(phase + i * 0.7f) * 3f
        val dx = sin(phase * 0.5f + i * 0.45f) * 1.6f
        drawOval(v.body, topLeft = Offset((x - 11 + dx) * s, (152 + dy) * s), size = Size(22 * s, 36 * s))
        drawCircle(v.bodyDark, radius = 2.5f * s, center = Offset((x + dx) * s, (178 + dy) * s))
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
    drawEye(80f, 100f, 6f, mood, s, blink)
    drawEye(120f, 100f, 6f, mood, s, blink)
    drawCircle(v.cheek.copy(alpha = 0.7f), radius = 7 * s, center = Offset(66 * s, 118 * s))
    drawCircle(v.cheek.copy(alpha = 0.7f), radius = 7 * s, center = Offset(134 * s, 118 * s))
    val mPath = if (mood == PetMood.HAPPY)
        Path().apply { moveTo(88 * s, 122 * s); quadraticTo(100 * s, 132 * s, 112 * s, 122 * s) }
    else
        Path().apply { moveTo(90 * s, 124 * s); quadraticTo(100 * s, 128 * s, 110 * s, 124 * s) }
    drawPath(mPath, Ink, style = Stroke(width = 2.5f * s, cap = StrokeCap.Round))
}
