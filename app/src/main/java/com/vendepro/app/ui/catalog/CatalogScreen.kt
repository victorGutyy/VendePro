package com.vendepro.app.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.vendepro.app.data.model.Product
import com.vendepro.app.ui.VendeProViewModel
import com.vendepro.app.ui.components.CatalogEmptyState
import com.vendepro.app.ui.theme.Lime
import com.vendepro.app.ui.theme.OnBackgroundMuted
import com.vendepro.app.ui.theme.OnLime
import com.vendepro.app.ui.theme.Pink
import com.vendepro.app.ui.theme.SurfaceHigh
import com.vendepro.app.utils.ShareHelper

@Composable
fun CatalogScreen(
    onAddProduct: () -> Unit,
    onProductClick: (Int) -> Unit,
    onSettings: () -> Unit,
    viewModel: VendeProViewModel = viewModel(),
) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsStateWithLifecycle()
    val config by viewModel.businessConfig.collectAsStateWithLifecycle()
    var productPendingDelete by remember { mutableStateOf<Product?>(null) }

    val businessName = config?.businessName?.takeIf { it.isNotBlank() } ?: "Mi Emprendimiento"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CatalogHeader(
                businessName = businessName,
                productCount = products.size,
                onSettings = onSettings,
                onShareWhatsApp = {
                    if (products.isNotEmpty()) {
                        ShareHelper.generateCatalogCard(context, products)
                            ?.let { ShareHelper.shareToWhatsApp(context, it, "🛍️ Catálogo") }
                    }
                },
                onShareInstagram = {
                    if (products.isNotEmpty()) {
                        ShareHelper.generateCatalogCard(context, products)
                            ?.let { ShareHelper.shareToInstagram(context, it) }
                    }
                },
                onShareMore = {
                    if (products.isNotEmpty()) {
                        ShareHelper.generateCatalogCard(context, products)
                            ?.let { ShareHelper.shareGeneral(context, it, "Catálogo disponible") }
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (products.isEmpty()) {
                CatalogEmptyState(businessName = businessName, onAddProduct = onAddProduct)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(13.dp),
                    verticalArrangement = Arrangement.spacedBy(13.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(products, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            onShare = {
                                ShareHelper.generateProductCard(context, product)?.let { uri ->
                                    ShareHelper.shareGeneral(context, uri, ShareHelper.buildProductText(product))
                                }
                            },
                            onSharePersonal = { ShareHelper.shareProductToClient(context, product) },
                            onDeleteRequest = { productPendingDelete = product },
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 22.dp)
                    .clip(RoundedCornerShape(0.dp))
                    .background(Lime)
                    .clickable(onClick = onAddProduct)
                    .padding(horizontal = 26.dp, vertical = 15.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = null,
                        tint = OnLime,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PUBLICAR PRODUCTO",
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 16.sp),
                        color = OnLime,
                    )
                }
            }
        }
    }

    productPendingDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productPendingDelete = null },
            title = { Text("Eliminar producto") },
            text = { Text("¿Seguro que quieres eliminar \"${product.productName}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(product)
                    productPendingDelete = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { productPendingDelete = null }) { Text("Cancelar") }
            },
            containerColor = SurfaceHigh,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            textContentColor = OnBackgroundMuted,
        )
    }
}
