package com.vendepro.app.ui.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vendepro.app.R
import com.vendepro.app.data.model.Product
import com.vendepro.app.ui.components.ProductImage
import com.vendepro.app.ui.theme.Danger
import com.vendepro.app.ui.theme.Lime
import com.vendepro.app.ui.theme.OnLime
import com.vendepro.app.ui.theme.Outline
import com.vendepro.app.ui.theme.Pink
import java.text.NumberFormat
import java.util.Locale

private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
private const val NEW_WINDOW_MS = 48 * 60 * 60 * 1000L

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onShare: () -> Unit,
    onSharePersonal: () -> Unit,
    onDeleteRequest: () -> Unit,
) {
    val isNew = System.currentTimeMillis() - product.uploadedAt < NEW_WINDOW_MS

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, Outline))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
    ) {
        Box {
            ProductImage(
                path = product.imagePath1,
                seed = product.id,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f),
            )
            if (isNew) {
                Box(
                    modifier = Modifier
                        .padding(9.dp)
                        .background(Pink)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text("NUEVO", style = MaterialTheme.typography.labelSmall, color = OnLime)
                }
            }
        }

        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = product.productName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = currencyFormat.format(product.price),
                style = MaterialTheme.typography.displaySmall,
                color = Lime,
                modifier = Modifier.padding(top = 3.dp),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp),
            ) {
                CardIconAction(
                    iconRes = R.drawable.ic_share,
                    contentDescription = "Compartir",
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                )
                CardTextAction(
                    label = "ENVIAR",
                    onClick = onSharePersonal,
                    modifier = Modifier.weight(1f),
                )
                CardIconAction(
                    iconRes = R.drawable.ic_delete,
                    contentDescription = "Eliminar",
                    tint = Danger,
                    onClick = onDeleteRequest,
                )
            }
        }
    }
}

@Composable
private fun RowScope.CardIconAction(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: androidx.compose.ui.graphics.Color = Lime,
) {
    Box(
        modifier = modifier
            .height(34.dp)
            .border(BorderStroke(1.dp, Outline))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun RowScope.CardTextAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(34.dp)
            .background(Lime)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = OnLime)
    }
}
