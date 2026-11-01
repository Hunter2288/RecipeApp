package com.example.recipeapp.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourname.savor.ui.theme.DotInactive
import com.yourname.savor.ui.theme.PillShape
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recipeapp.shared.generated.resources.*

private data class OnboardingPage(
    val emoji: String,
    val title: StringResource,
    val text: StringResource,
)

private val pages = listOf(
    OnboardingPage("🍽️", Res.string.ob_title_1, Res.string.ob_text_1),
    OnboardingPage("🥕", Res.string.ob_title_2, Res.string.ob_text_2),
    OnboardingPage("❤️", Res.string.ob_title_3, Res.string.ob_text_3),
    OnboardingPage("⏱️", Res.string.ob_title_4, Res.string.ob_text_4),
)

@Composable
fun OnboardingContainer(
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                OnboardingEffect.NavigateToAuth -> onNavigateToAuth()
                OnboardingEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    OnboardingScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onIntent: (OnboardingIntent) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = state.currentPage, pageCount = { pages.size })

    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { onIntent(OnboardingIntent.PageChanged(it)) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.CenterEnd) {
            if (!state.isLast) {
                TextButton(onClick = { onIntent(OnboardingIntent.Skip) }) {
                    Text(stringResource(Res.string.skip), style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) { index -> OnboardingPageContent(pages[index]) }

        PageIndicator(count = pages.size, current = state.currentPage)

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { onIntent(OnboardingIntent.Next) },
            shape = PillShape,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            Text(
                text = stringResource(if (state.isLast) Res.string.start else Res.string.next),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) { Text(page.emoji, fontSize = 84.sp) }
        Spacer(Modifier.height(40.dp))
        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(page.text),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PageIndicator(count: Int, current: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(count) { i ->
            val selected = i == current
            val width by animateDpAsState(if (selected) 28.dp else 8.dp)
            Box(
                Modifier.height(8.dp).width(width).clip(PillShape)
                    .background(if (selected) MaterialTheme.colorScheme.primary else DotInactive)
            )
        }
    }
}
