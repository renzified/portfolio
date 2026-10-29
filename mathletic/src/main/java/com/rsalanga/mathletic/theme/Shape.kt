package com.rsalanga.mathletic.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Corner sizes taken from the designs. M3 components pick these up
// automatically (buttons use full/large, cards medium/large, sheets extraLarge).
val MathleticShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp), // connected button-group inner corners
    small = RoundedCornerShape(12.dp), // chips, small icon tiles
    medium = RoundedCornerShape(20.dp), // stat cards, list rows, keypad keys
    large = RoundedCornerShape(28.dp), // mode rows, skill card, HUD card
    extraLarge = RoundedCornerShape(32.dp) // bottom sheets, hero, question card
)

// Expressive asymmetric shapes used on the 2×2 mode tiles and choice buttons.
// Pair with MaterialShapes (e.g. MaterialShapes.Cookie9Sided) for the date badge
// and skill glyph, and animate selected states between shapes for the
// "shape morph" feel.
object MathleticCornerShapes {
    val TileTopLeft = RoundedCornerShape(32.dp, 32.dp, 32.dp, 12.dp)
    val TileTopRight = RoundedCornerShape(32.dp, 32.dp, 12.dp, 32.dp)
    val TileBottomLeft = RoundedCornerShape(32.dp, 12.dp, 32.dp, 32.dp)
    val TileBottomRight = RoundedCornerShape(12.dp, 32.dp, 32.dp, 32.dp)

    // Stacked list (Home modes): round outer corners, tight inner ones.
    val ListTop = RoundedCornerShape(24.dp, 24.dp, 8.dp, 8.dp)
    val ListMiddle = RoundedCornerShape(8.dp)
    val ListBottom = RoundedCornerShape(8.dp, 8.dp, 24.dp, 24.dp)
}
