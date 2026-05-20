package com.octopet.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.octopet.app.OAuthStep
import com.octopet.app.UiState
import com.octopet.app.data.PetMood
import com.octopet.app.data.PetStage
import com.octopet.app.ui.components.PaperBg
import com.octopet.app.ui.creature.OctoPet
import com.octopet.app.ui.theme.*

@Composable
fun OnboardingScreen(
    uiState: UiState,
    onStartOAuth: () -> Unit,
) {
    var step by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    val steps = listOf(
        OnboardingStep(
            title = "Meet your\nOcto-pet.",
            sub = "A little companion that grows every time you ship code.",
            stage = PetStage.EGG,
            cta = "Continue",
        ),
        OnboardingStep(
            title = "Commit.\nGrow.\nRepeat.",
            sub = "Your pet evolves through 5 stages as you hit contribution milestones.",
            stage = PetStage.SPROUT,
            cta = "I'm in",
        ),
        OnboardingStep(
            title = "Connect\nGitHub",
            sub = "Authorize with GitHub to sync your real contribution data — public and private.",
            stage = null,
            cta = "Connect with GitHub",
        ),
    )

    val s = steps[step]
    val isLastStep = step == steps.size - 1

    PaperBg {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 28.dp),
        ) {
            Spacer(Modifier.height(24.dp))

            // Progress dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                steps.forEachIndexed { i, _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (i <= step) Ink else PaperLine)
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Creature or GitHub icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (s.stage != null) {
                    OctoPet(stage = s.stage, mood = PetMood.HAPPY, size = 200.dp)
                } else {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Ink),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("GH", color = Paper, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = s.title,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 42.sp,
                letterSpacing = (-1.2).sp,
                color = Ink,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = s.sub,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = InkMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            )

            // Stage pills on step 2
            if (step == 1) {
                Spacer(Modifier.height(28.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                ) {
                    PetStage.entries.forEachIndexed { i, st ->
                        val highlighted = i == 1
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    if (highlighted) Moss else PaperLine,
                                    RoundedCornerShape(12.dp),
                                )
                                .background(if (highlighted) MossSoft else Color.Transparent)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        ) {
                            Text(
                                st.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Ink,
                                letterSpacing = 0.5.sp,
                            )
                            Text(
                                "${st.minContribs}+",
                                fontSize = 10.sp,
                                color = InkMuted,
                                fontFamily = JetBrainsMono,
                            )
                        }
                    }
                }
            }

            // OAuth flow UI on step 3
            if (isLastStep) {
                Spacer(Modifier.height(24.dp))
                when (uiState.oauthStep) {
                    OAuthStep.IDLE, OAuthStep.REQUESTING -> {
                        // Nothing extra shown — button below handles it
                    }
                    OAuthStep.AWAITING_AUTH -> {
                        // Show user code card
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                "Enter this code at:",
                                color = InkMuted,
                                fontSize = 14.sp,
                            )
                            Text(
                                uiState.verificationUri,
                                color = Ink,
                                fontFamily = JetBrainsMono,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Ink)
                                    .padding(vertical = 20.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = uiState.userCode,
                                    color = Paper,
                                    fontFamily = JetBrainsMono,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 6.sp,
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                CircularProgressIndicator(
                                    color = Ink,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    "Waiting for authorization…",
                                    color = InkMuted,
                                    fontSize = 13.sp,
                                )
                            }
                        }
                    }
                    OAuthStep.FETCHING_DATA -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            CircularProgressIndicator(color = Ink, strokeWidth = 2.dp)
                            Text("Fetching your contributions…", color = InkMuted, fontSize = 14.sp)
                        }
                    }
                }

                // Error message
                uiState.error?.let { err ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        err,
                        color = Coral,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // CTA button
            val isOAuthInProgress = isLastStep && uiState.oauthStep == OAuthStep.AWAITING_AUTH
            val isLoading = isLastStep && (uiState.oauthStep == OAuthStep.REQUESTING || uiState.oauthStep == OAuthStep.FETCHING_DATA)

            if (isOAuthInProgress) {
                // "Open in browser" shortcut button
                OutlinedButton(
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(uiState.verificationUri))
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Ink),
                    ),
                ) {
                    Text("Open github.com/login/device ↗", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Button(
                    onClick = {
                        if (!isLastStep) step++
                        else onStartOAuth()
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Ink,
                        contentColor = Paper,
                        disabledContainerColor = InkFaint,
                        disabledContentColor = Paper,
                    ),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Paper, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text(s.cta, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (step == 0) {
                TextButton(
                    onClick = { step = steps.size - 1 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                ) {
                    Text("Skip intro", color = InkMuted, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(44.dp))
        }
    }
}

private data class OnboardingStep(
    val title: String,
    val sub: String,
    val stage: PetStage?,
    val cta: String,
)
