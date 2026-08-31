package com.vendepro.app.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vendepro.app.R
import com.vendepro.app.ui.theme.Lime
import com.vendepro.app.ui.theme.OnBackgroundMuted
import com.vendepro.app.ui.theme.OnLime
import com.vendepro.app.ui.theme.SurfaceHigh

@Composable
fun CatalogHeader(
    businessName: String,
    productCount: Int,
    onSettings: () -> Unit,
    onShareWhatsApp: () -> Unit,
    onShareInstagram: () -> Unit,
    onShareMore: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 22.dp, end = 22.dp, top = 30.dp, bottom = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = businessName.uppercase(),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceHigh)
                    .clickable(onClick = onSettings),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = "Configuración",
                    tint = Lime,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Text(
            text = "$productCount ${if (productCount == 1) "PRODUCTO" else "PRODUCTOS"} · TU CATÁLOGO",
            style = MaterialTheme.typography.labelMedium,
            color = OnBackgroundMuted,
            modifier = Modifier.padding(top = 2.dp),
        )

        if (productCount > 0) {
            Row(modifier = Modifier.padding(top = 14.dp)) {
                ShareChip(label = "WHATSAPP", color = Color(0xFF25D366), onClick = onShareWhatsApp)
                Box(modifier = Modifier.padding(start = 8.dp))
                ShareChip(label = "INSTAGRAM", color = Color(0xFFE1306C), onClick = onShareInstagram)
                Box(modifier = Modifier.padding(start = 8.dp))
                ShareChip(label = "MÁS", color = Lime, onClick = onShareMore)
            }
        }
    }
}

@Composable
private fun ShareChip(label: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(0.dp))
            .background(SurfaceHigh)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, RoundedCornerShape(50)),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}
