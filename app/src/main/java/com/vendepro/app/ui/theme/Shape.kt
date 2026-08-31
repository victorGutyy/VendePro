package com.vendepro.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Identidad "Hustle Neón": esquinas afiladas en casi todo — el único
// redondeo vive en los botones-ícono chicos, para que no se sientan
// puntiagudos al tacto.
val VendeProShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp),
)
