package com.rsalanga.mathletic.pages.home.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rsalanga.mathletic.R
import com.rsalanga.mathletic.theme.MathleticTheme

enum class BottomTabs { PLAY, SKILLS, RANKS, PROFILE }

@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    onClick: (BottomTabs) -> Unit,
    selected: BottomTabs
) {
    val inset = WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
        .asPaddingValues()

    Surface(modifier = modifier, color = MaterialTheme.colorScheme.surfaceContainer) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(top = 12.dp, bottom = 20.dp)
                .padding(inset)
        ) {
            MenuItem(
                modifier = Modifier.weight(1f),
                isSelected = selected == BottomTabs.PLAY,
                onClick = { onClick(BottomTabs.PLAY) },
                label = R.string.play,
                defaultIcon = R.drawable.ic_bolt_outline,
                selectedIcon = R.drawable.ic_bolt_filled
            )

            MenuItem(
                modifier = Modifier.weight(1f),
                isSelected = selected == BottomTabs.SKILLS,
                onClick = { onClick(BottomTabs.SKILLS) },
                label = R.string.skills,
                defaultIcon = R.drawable.ic_skills_outline,
                selectedIcon = R.drawable.ic_skills_filled
            )

            MenuItem(
                modifier = Modifier.weight(1f),
                isSelected = selected == BottomTabs.RANKS,
                onClick = { onClick(BottomTabs.RANKS) },
                label = R.string.ranks,
                defaultIcon = R.drawable.ic_skills_outline,
                selectedIcon = R.drawable.ic_skills_filled
            )

            MenuItem(
                modifier = Modifier.weight(1f),
                isSelected = selected == BottomTabs.PROFILE,
                onClick = { onClick(BottomTabs.PROFILE) },
                label = R.string.profile,
                defaultIcon = R.drawable.ic_skills_outline,
                selectedIcon = R.drawable.ic_skills_filled
            )
        }
    }
}

@Preview(
    name = "Light Bottom Navigation",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Bottom Navigation",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewBottomNavigation() {
    MathleticTheme {
        var navigation by remember { mutableStateOf(BottomTabs.PLAY) }

        BottomNavigation(
            onClick = { navigation = it },
            selected = navigation
        )
    }
}

@Composable
private fun MenuItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onClick: () -> Unit,
    @StringRes label: Int,
    @DrawableRes defaultIcon: Int,
    @DrawableRes selectedIcon: Int
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val containerColor = if (isSelected) {
            colorScheme.inverseSurface
        } else {
            Color.Transparent
        }
        val contentColor = if (isSelected) {
            colorScheme.inverseOnSurface
        } else {
            colorScheme.onSurfaceVariant
        }

        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(24.dp),
            color = containerColor,
            contentColor = contentColor
        ) {
            Column(
                modifier = Modifier
                    .height(48.dp)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val icon = if (isSelected) {
                    selectedIcon
                } else {
                    defaultIcon
                }

                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(label),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview(
    name = "Selected Menu Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Selected Menu Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewSelectedMenuItem() {
    MathleticTheme {
        MenuItem(
            isSelected = true,
            onClick = {},
            label = R.string.play,
            defaultIcon = R.drawable.ic_bolt_outline,
            selectedIcon = R.drawable.ic_bolt_filled
        )
    }
}

@Preview(
    name = "Unselected Menu Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Unselected Menu Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewUnselectedMenuItem() {
    MathleticTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            MenuItem(
                isSelected = false,
                onClick = {},
                label = R.string.play,
                defaultIcon = R.drawable.ic_bolt_outline,
                selectedIcon = R.drawable.ic_bolt_filled
            )
        }
    }
}