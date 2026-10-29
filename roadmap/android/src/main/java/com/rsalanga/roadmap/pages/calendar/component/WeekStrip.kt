package com.rsalanga.roadmap.pages.calendar.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rsalanga.roadmap.ui.RoadmapTheme

@Composable
internal fun CalendarWeekStrip() {
    Row {
        Column {
            Text(text = "Mon")
            Text(text = "26")
        }
    }
}

@Preview
@Composable
private fun PreviewCalendarWeekStrip() {
    RoadmapTheme {
        CalendarWeekStrip()
    }
}
