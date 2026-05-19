package com.octopet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.octopet.app.data.PetMood
import com.octopet.app.data.PetStage
import com.octopet.app.data.PetFamily
import com.octopet.app.ui.components.PaperBg
import com.octopet.app.ui.creature.OctoPet
import com.octopet.app.ui.theme.*

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    var username by remember { mutableStateOf("") }

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
            sub = "We'll read your public contribution graph. Nothing else.",
            stage = null,
            cta = "Hatch my pet",
            showInput = true,
        ),
    )

    val s = steps[step]

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
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
                    OctoPet(
                        stage = s.stage,
                        mood = PetMood.HAPPY,
                        size = 200.dp,
                    )
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
                                text = st.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Ink,
                                letterSpacing = 0.5.sp,
                            )
                            Text(
                                text = "${st.minContribs}+",
                                fontSize = 10.sp,
                                color = InkMuted,
                                fontFamily = JetBrainsMono,
                            )
                        }
                    }
                }
            }

            // GitHub username input on step 3
            if (s.showInput) {
                Spacer(Modifier.height(28.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, PaperLine, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "github.com/",
                        color = InkMuted,
                        fontFamily = JetBrainsMono,
                        fontSize = 15.sp,
                    )
                    BasicTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier.weight(1f),
                        textStyle = LocalTextStyle.current.copy(
                            color = Ink,
                            fontFamily = JetBrainsMono,
                            fontSize = 15.sp,
                        ),
                        decorationBox = { inner ->
                            if (username.isEmpty()) {
                                Text("your-username", color = InkFaint, fontFamily = JetBrainsMono, fontSize = 15.sp)
                            }
                            inner()
                        },
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // CTA button
            val canProceed = !s.showInput || username.isNotEmpty()
            Button(
                onClick = {
                    if (step < steps.size - 1) step++ else onComplete()
                },
                enabled = canProceed,
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
                Text(s.cta, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
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
    val showInput: Boolean = false,
)
