package com.octopet.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class OctopetTokens(
    val paper: Color = Paper,
    val paperDeep: Color = PaperDeep,
    val paperLine: Color = PaperLine,
    val ink: Color = Ink,
    val inkSoft: Color = InkSoft,
    val inkMuted: Color = InkMuted,
    val inkFaint: Color = InkFaint,
    val moss: Color = Moss,
    val mossDeep: Color = MossDeep,
    val mossSoft: Color = MossSoft,
    val coral: Color = Coral,
    val coralSoft: Color = CoralSoft,
    val lavender: Color = Lavender,
    val lavenderSoft: Color = LavenderSoft,
    val butter: Color = Butter,
    val butterSoft: Color = ButterSoft,
    val heat0: Color = Heat0,
    val heat1: Color = Heat1,
    val heat2: Color = Heat2,
    val heat3: Color = Heat3,
    val heat4: Color = Heat4,
)

val LocalOctopetTokens = staticCompositionLocalOf { OctopetTokens() }

val OctopetColorScheme = lightColorScheme(
    primary = Ink,
    onPrimary = Paper,
    primaryContainer = MossSoft,
    onPrimaryContainer = MossDeep,
    secondary = Moss,
    onSecondary = Paper,
    background = Paper,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = PaperDeep,
    onSurfaceVariant = InkSoft,
    outline = PaperLine,
)

@Composable
fun OctopetTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalOctopetTokens provides OctopetTokens()) {
        MaterialTheme(
            colorScheme = OctopetColorScheme,
            typography = OctopetTypography,
            content = content,
        )
    }
}

// Shorthand accessor inside composables
val tokens @Composable get() = LocalOctopetTokens.current
