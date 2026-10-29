package com.rsalanga.roadmap.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Corner radii from the designs: 8–12 dp chips, 20 dp event cards, 28 dp grouped cards,
// 32–36 dp featured/hero cards.
val RoadmapShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/** Expressive shapes the design uses beyond the five M3 slots. */
object RoadmapShapeTokens {
    /** Between medium and large — chat cards, tip and check-in cards. */
    val LargeReduced = RoundedCornerShape(24.dp)

    /** Hero card on event details. */
    val ExtraLargeIncreased = RoundedCornerShape(36.dp)

    /** Extended FAB ("Learn a skill", "New plan"). */
    val Fab = RoundedCornerShape(22.dp)

    /** Selected day in the week strip — rounded with a tucked bottom-start corner. */
    val SelectedDay = RoundedCornerShape(
        topStart = 28.dp,
        topEnd = 28.dp,
        bottomEnd = 28.dp,
        bottomStart = 10.dp
    )

    /** Outgoing chat bubble (user → Coach). */
    val OutgoingBubble = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 24.dp,
        bottomEnd = 6.dp,
        bottomStart = 24.dp
    )

    // Segmented list groups (Settings, Event details): outer corners 24 dp, inner 6 dp.
    val ListItemTop =
        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomEnd = 6.dp, bottomStart = 6.dp)
    val ListItemMiddle = RoundedCornerShape(6.dp)
    val ListItemBottom =
        RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomEnd = 24.dp, bottomStart = 24.dp)
}
