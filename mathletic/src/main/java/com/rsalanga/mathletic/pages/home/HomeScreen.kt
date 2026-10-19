package com.rsalanga.mathletic.pages.home

import android.content.res.Configuration
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.rsalanga.mathletic.pages.home.components.BottomNavigation
import com.rsalanga.mathletic.pages.home.components.BottomTabs
import com.rsalanga.mathletic.theme.MathleticTheme

@Composable
fun HomeScreen() {
    Scaffold(
        bottomBar = {
            var navigation by remember { mutableStateOf(BottomTabs.PLAY) }
            BottomNavigation(
                onClick = { navigation = it },
                selected = navigation
            )
        }
    ) { paddingValues ->

    }
}

@Preview(
    name = "Light",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewHomeScreen() {
    MathleticTheme {
        HomeScreen()
    }
}