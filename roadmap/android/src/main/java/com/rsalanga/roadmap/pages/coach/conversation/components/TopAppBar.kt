package com.rsalanga.roadmap.pages.coach.conversation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rsalanga.roadmap.R
import com.rsalanga.roadmap.ui.RoadmapTheme

@Composable
internal fun ConversationTopAppBar(
    onBack: () -> Unit
) {
    Surface {
        Column {
            Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars))
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Text(text = "Coach", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "New plan · Spanish",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more_vert),
                        contentDescription = null
                    )
                }
            }

            Column {
                Spacer(modifier = Modifier.height(12.dp))
                LinearWavyProgressIndicator(
                    progress = { 0.5f },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "STEP 1 OF 5 · SKILL",
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewConversationTopAppBar() {
    RoadmapTheme {
        ConversationTopAppBar(onBack = {})
    }
}