package com.vendepro.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import java.io.File

/**
 * Foto de producto con degradado de respaldo cuando no hay imagen
 * (o el archivo ya no existe) — nunca un ícono roto.
 */
@Composable
fun ProductImage(
    path: String,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(0.dp),
    seed: Int = path.hashCode(),
) {
    val file = if (path.isNotBlank()) File(path) else null
    if (file != null && file.exists()) {
        AsyncImage(
            model = file,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(shape),
        )
    } else {
        GradientPlaceholder(seed = seed, modifier = modifier.clip(shape))
    }
}

@Composable
fun GradientPlaceholder(seed: Int, modifier: Modifier = Modifier) {
    val palette = listOf(
        Color(0xFF555075) to Color(0xFF322843),
        Color(0xFF473B5E) to Color(0xFF1F1C31),
        Color(0xFF4A3A56) to Color(0xFF241E33),
        Color(0xFF3D3452) to Color(0xFF201B2D),
    )
    val (a, b) = palette[Math.floorMod(seed, palette.size)]
    Box(modifier = modifier.background(Brush.linearGradient(listOf(a, b))))
}
