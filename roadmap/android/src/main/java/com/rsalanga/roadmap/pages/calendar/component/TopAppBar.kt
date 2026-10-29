package com.rsalanga.roadmap.pages.calendar.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rsalanga.roadmap.R
import com.rsalanga.roadmap.ui.RoadmapTheme

@Composable
internal fun CalendarScreenTopAppBar(
    title: String,
    yearAndWeek: String
) {
    Surface {
        Column {
            Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = title, style = MaterialTheme.typography.headlineLarge)
                    Text(text = yearAndWeek, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal))
                }

                Spacer(modifier = Modifier.weight(1f))

                Row {
                    IconButton(onClick = {}) { Icon(painter = painterResource(R.drawable.ic_search), contentDescription = null) }
                    Spacer(modifier = Modifier.width(4.dp))
                    FilledTonalButton(onClick = {}) { Text(text = "Today") }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCalendarScreenTopAppBar() {
    RoadmapTheme {
        CalendarScreenTopAppBar(
            title = "October",
            yearAndWeek = "2026 · Week 44"
        )
    }
}
