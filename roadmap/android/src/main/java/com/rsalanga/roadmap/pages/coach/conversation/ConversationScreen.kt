package com.rsalanga.roadmap.pages.coach.conversation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rsalanga.roadmap.pages.coach.conversation.components.ConversationTopAppBar
import com.rsalanga.roadmap.ui.RoadmapTheme

@Composable
internal fun ConversationScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            ConversationTopAppBar(onBack = onBack)
        },
        contentWindowInsets = WindowInsets()
    ) { innerPadding ->

    }
}

@Preview
@Composable
private fun PreviewConversationScreen() {
    RoadmapTheme {
        ConversationScreen(onBack = {})
    }
}