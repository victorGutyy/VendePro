package com.vendepro.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vendepro.app.R
import com.vendepro.app.ui.theme.Lime
import com.vendepro.app.ui.theme.OnLime
import com.vendepro.app.ui.theme.OnBackgroundMuted
import com.vendepro.app.ui.theme.SurfaceHigh

/**
 * Primer contacto con la app sin productos todavía: hace de "onboarding"
 * liviano — explica en una frase qué hacer y ofrece la acción directa,
 * en vez de una grilla vacía sin contexto.
 */
@Composable
fun CatalogEmptyState(
    businessName: String,
    onAddProduct: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.linearGradient(listOf(SurfaceHigh, Lime.copy(alpha = 0.18f)))),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.material3.Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
                tint = Lime,
                modifier = Modifier.size(30.dp),
            )
        }

        Column(
            modifier = Modifier.padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "TU VITRINA ESTÁ VACÍA",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "$businessName todavía no tiene productos publicados. Agregá el primero y empezá a compartirlo por WhatsApp e Instagram.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnBackgroundMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp),
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 28.dp)
                .clip(RoundedCornerShape(0.dp))
                .background(Lime)
                .clickable(onClick = onAddProduct)
                .padding(horizontal = 28.dp, vertical = 15.dp),
        ) {
            Text(
                text = "PUBLICAR EL PRIMER PRODUCTO",
                style = MaterialTheme.typography.labelLarge,
                color = OnLime,
            )
        }
    }
}
