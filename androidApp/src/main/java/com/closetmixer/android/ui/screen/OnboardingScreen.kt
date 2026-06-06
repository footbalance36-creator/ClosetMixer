package com.closetmixer.android.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.FlightTakeoff
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val BackgroundDark = Color(0xFF1A1C1B)
private val GoldLight      = Color(0xFFE9C349)
private val GoldPale       = Color(0xFF574500)
private val TextWhite      = Color(0xFFF5F3EE)
private val TextMuted      = Color(0xFFADAA9F)

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private val pages = listOf(
    OnboardingPage(
        icon = Icons.Outlined.Style,
        title = "Bienvenue sur ClosetMixer",
        description = "Votre garde-robe digitale, pensée pour le quotidien.\nCatalogisez, mixez, planifiez."
    ),
    OnboardingPage(
        icon = Icons.Outlined.AddPhotoAlternate,
        title = "Cataloguez vos articles",
        description = "Photographiez chaque pièce. Choisissez catégorie, couleur et style culturel pour un catalogue complet."
    ),
    OnboardingPage(
        icon = Icons.Default.AutoAwesome,
        title = "Votre tenue du jour",
        description = "L'algorithme compose une tenue adaptée à la météo locale, à l'occasion et à votre identité vestimentaire."
    ),
    OnboardingPage(
        icon = Icons.Outlined.FlightTakeoff,
        title = "Préparez vos voyages",
        description = "Créez une valise virtuelle pour chaque voyage et glissez-y vos articles en quelques taps."
    ),
    OnboardingPage(
        icon = Icons.Outlined.Share,
        title = "Partagez votre style",
        description = "Envoyez vos tenues sur Instagram, WhatsApp ou tout autre réseau directement depuis l'application."
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // ── Skip button ────────────────────────────────────────────────────
        if (!isLastPage) {
            TextButton(
                onClick = onFinish,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(end = 8.dp)
            ) {
                Text(
                    "Ignorer",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }
        }

        // ── Pages ──────────────────────────────────────────────────────────
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            PageContent(page = pages[pageIndex])
        }

        // ── Bottom controls ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.indices.forEach { index ->
                    val isActive = index == pagerState.currentPage
                    val dotColor by animateColorAsState(
                        targetValue = if (isActive) GoldLight else GoldPale,
                        animationSpec = tween(300),
                        label = "dot"
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(dotColor)
                            .then(
                                if (isActive) Modifier.width(24.dp).height(8.dp)
                                else Modifier.size(8.dp)
                            )
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Next / Start button
            Button(
                onClick = {
                    if (isLastPage) onFinish()
                    else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldLight,
                    contentColor = Color(0xFF1A1C1B)
                )
            ) {
                Text(
                    text = if (isLastPage) "COMMENCER" else "SUIVANT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
private fun PageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(GoldPale),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                tint = GoldLight,
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(Modifier.height(48.dp))

        Text(
            text = page.title,
            color = TextWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = page.description,
            color = TextMuted,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        // Reserve space for bottom controls (dots + button)
        Spacer(Modifier.height(160.dp))
    }
}
