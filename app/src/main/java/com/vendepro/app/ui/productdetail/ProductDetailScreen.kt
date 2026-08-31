package com.vendepro.app.ui.productdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vendepro.app.R
import com.vendepro.app.ui.VendeProViewModel
import com.vendepro.app.ui.components.ProductImage
import com.vendepro.app.ui.theme.Danger
import com.vendepro.app.ui.theme.Lime
import com.vendepro.app.ui.theme.OnBackgroundMuted
import com.vendepro.app.ui.theme.OnLime
import com.vendepro.app.ui.theme.Outline
import com.vendepro.app.ui.theme.SurfaceHigh
import com.vendepro.app.utils.ShareHelper
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductDetailScreen(
    productId: Int,
    onBack: () -> Unit,
    viewModel: VendeProViewModel = viewModel(),
) {
    val context = LocalContext.current
    val productFlow = remember(productId) { viewModel.productById(productId) }
    val product by productFlow.collectAsStateWithLifecycle(initialValue = null)
    var confirmDelete by remember { mutableStateOf(false) }
    val fmt = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")) }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        val p = product
        if (p == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Producto no disponible", color = OnBackgroundMuted, style = MaterialTheme.typography.bodyMedium)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Box {
                ProductImage(
                    path = p.imagePath1,
                    seed = p.id,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1.1f),
                )
                Box(
                    modifier = Modifier
                        .padding(14.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceHigh)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Volver",
                        tint = Lime,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            val extraPhotos = listOf(p.imagePath2, p.imagePath3, p.imagePath4).filter { it.isNotBlank() }
            if (extraPhotos.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 22.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    extraPhotos.forEach { path ->
                        ProductImage(path = path, modifier = Modifier.size(64.dp))
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 6.dp)) {
                Text(p.productName, style = MaterialTheme.typography.displaySmall.copy(fontSize = 26.sp), color = MaterialTheme.colorScheme.onBackground)

                if (p.description.isNotBlank()) {
                    Text(
                        text = p.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnBackgroundMuted,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                Text(
                    text = fmt.format(p.price),
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 32.sp),
                    color = Lime,
                    modifier = Modifier.padding(top = 12.dp),
                )

                DetailDivider()

                Text("VENDEDOR", style = MaterialTheme.typography.labelMedium, color = Lime)
                Text(
                    p.sellerName.ifBlank { "—" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp),
                )

                Text("CONTACTO Y PAGOS", style = MaterialTheme.typography.labelMedium, color = Lime)
                Text(
                    "Tel: ${p.contactNumber.ifBlank { "—" }}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 4.dp),
                )
                if (p.nequiAccount.isNotBlank()) {
                    Text("Nequi: ${p.nequiAccount}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                }
                if (p.daviplataAccount.isNotBlank()) {
                    Text("Daviplata: ${p.daviplataAccount}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                }

                DetailDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .background(Lime)
                            .clickable { ShareHelper.shareProductToClient(context, p) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("ENVIAR A CLIENTE", style = MaterialTheme.typography.labelLarge, color = OnLime)
                    }
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .background(androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { confirmDelete = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = "Eliminar",
                            tint = Danger,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    if (confirmDelete) {
        val p = product
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Eliminar producto") },
            text = { Text("¿Seguro que quieres eliminar \"${p?.productName}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    p?.let { viewModel.delete(it) }
                    confirmDelete = false
                    onBack()
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") }
            },
            containerColor = SurfaceHigh,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            textContentColor = OnBackgroundMuted,
        )
    }
}

@Composable
private fun DetailDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .height(1.dp)
            .background(Outline),
    )
}
