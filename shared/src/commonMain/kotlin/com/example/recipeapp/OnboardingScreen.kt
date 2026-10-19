package com.example.recipeapp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import com.yourname.savor.ui.theme.DotInactive
import com.yourname.savor.ui.theme.PillShape
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import recipeapp.shared.generated.resources.*

private data class OnboardingPage(
    val emoji: String,
    val title: StringResource,
    val text: StringResource,
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val log = remember { Logger.withTag("Onboarding") }

    val pages = remember {
        listOf(
            OnboardingPage("🍽️", Res.string.ob_title_1, Res.string.ob_text_1),
            OnboardingPage("🥕", Res.string.ob_title_2, Res.string.ob_text_2),
            OnboardingPage("❤️", Res.string.ob_title_3, Res.string.ob_text_3),
            OnboardingPage("⏱️", Res.string.ob_title_4, Res.string.ob_text_4),
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == pages.lastIndex

    LaunchedEffect(Unit) { log.i { "Onboarding opened" } }
    LaunchedEffect(pagerState.currentPage) {
        log.d { "Onboarding page changed: ${pagerState.currentPage + 1}/${pages.size}" }
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
            if (!isLast) {
                TextButton(onClick = {
                    log.i { "Onboarding skipped on page ${pagerState.currentPage + 1}" }
                    onFinish()
                }) {
                    Text(stringResource(Res.string.skip), style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) { index ->
            OnboardingPageContent(pages[index])
        }

        PageIndicator(count = pages.size, current = pagerState.currentPage)

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (isLast) {
                    log.i { "Onboarding finished" }
                    onFinish()
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            },
            shape = PillShape,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            Text(
                text = stringResource(if (isLast) Res.string.start else Res.string.next),
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
        ) {
            Text(page.emoji, fontSize = 84.sp)
        }
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
            val width = animateDpAsState(if (selected) 28.dp else 8.dp).value
            Box(
                Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(PillShape)
                    .background(if (selected) MaterialTheme.colorScheme.primary else DotInactive)
            )
        }
    }
}