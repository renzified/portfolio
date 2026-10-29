package com.rsalanga.roadmap.pages.calendar

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rsalanga.roadmap.R
import com.rsalanga.roadmap.pages.calendar.component.CalendarScreenTopAppBar
import com.rsalanga.roadmap.ui.RoadmapShapeTokens
import com.rsalanga.roadmap.ui.RoadmapTheme

@Composable
internal fun CalendarScreen(
    onClickLearnASkill: () -> Unit
) {
    Scaffold(
        topBar = {
            CalendarScreenTopAppBar(
                title = "October",
                yearAndWeek = "2026 · Week 44"
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onClickLearnASkill,
                shape = RoadmapShapeTokens.Fab
            ) {
                Icon(painter = painterResource(R.drawable.ic_sparkles), contentDescription = null)
                Text(text = stringResource(R.string.learn_a_skill))
            }
        }
    ) { _ ->

    }
}

@Preview
@Composable
private fun CalendarScreenPreview() {
    RoadmapTheme {
        CalendarScreen(onClickLearnASkill = {})
    }
}