package com.vendepro.app.ui.addproduct

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vendepro.app.R
import com.vendepro.app.data.model.Product
import com.vendepro.app.ui.VendeProViewModel
import com.vendepro.app.ui.components.ProductImage
import com.vendepro.app.ui.theme.Danger
import com.vendepro.app.ui.theme.Lime
import com.vendepro.app.ui.theme.OnBackgroundMuted
import com.vendepro.app.ui.theme.OnLime
import com.vendepro.app.ui.theme.Outline
import com.vendepro.app.ui.theme.Pink
import com.vendepro.app.ui.theme.SurfaceHigh
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddProductScreen(
    onBack: () -> Unit,
    viewModel: VendeProViewModel = viewModel(),
) {
    val context = LocalContext.current
    val config by viewModel.businessConfig.collectAsStateWithLifecycle()

    val imagePaths = remember { mutableStateListOf("", "", "", "") }
    var pendingSlot by remember { mutableIntStateOf(-1) }
    var pendingCameraPath by remember { mutableStateOf("") }

    var businessName by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var sellerName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var nequi by remember { mutableStateOf("") }
    var daviplata by remember { mutableStateOf("") }

    LaunchedEffect(config) {
        config?.let {
            if (businessName.isBlank()) businessName = it.businessName
            if (sellerName.isBlank()) sellerName = it.sellerName
            if (contact.isBlank()) contact = it.contactNumber
            if (nequi.isBlank()) nequi = it.nequiAccount
            if (daviplata.isBlank()) daviplata = it.daviplataAccount
        }
    }

    fun toast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    fun nextFreeSlot() = imagePaths.indexOfFirst { it.isBlank() }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && pendingSlot >= 0 && pendingCameraPath.isNotEmpty()) {
            imagePaths[pendingSlot] = pendingCameraPath
        } else if (pendingCameraPath.isNotEmpty()) {
            File(pendingCameraPath).delete()
        }
        pendingSlot = -1
        pendingCameraPath = ""
    }

    val requestCameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val slot = nextFreeSlot()
            if (slot == -1) {
                toast("Máximo 4 fotos")
            } else {
                val file = createImageFile(context)
                pendingSlot = slot
                pendingCameraPath = file.absolutePath
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                takePicture.launch(uri)
            }
        } else {
            toast("Permiso de cámara denegado")
        }
    }

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val slot = nextFreeSlot()
            if (slot == -1) {
                toast("Máximo 4 fotos")
                return@let
            }
            val path = copyUriToAppPictures(context, it)
            if (path.isNullOrEmpty()) {
                toast("No se pudo guardar la imagen")
            } else {
                imagePaths[slot] = path
            }
        }
    }

    fun openCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val slot = nextFreeSlot()
            if (slot == -1) {
                toast("Máximo 4 fotos")
                return
            }
            val file = createImageFile(context)
            pendingSlot = slot
            pendingCameraPath = file.absolutePath
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            takePicture.launch(uri)
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    fun saveProduct() {
        val bName = businessName.trim()
        val pName = productName.trim()
        val priceText = price.trim()

        if (bName.isEmpty() || pName.isEmpty() || priceText.isEmpty()) {
            toast("Completa los campos obligatorios: Emprendimiento, Producto y Precio")
            return
        }
        // Acepta coma o punto decimal — el teclado numérico en es-CO suele mostrar coma.
        val priceValue = priceText.replace(',', '.').toDoubleOrNull()
        if (priceValue == null || priceValue <= 0.0) {
            toast("Ingresa un precio válido mayor a 0")
            return
        }
        if (imagePaths[0].isBlank()) {
            toast("Agrega mínimo 1 foto (Foto 1)")
            return
        }

        viewModel.insert(
            Product(
                businessName = bName,
                productName = pName,
                description = description.trim(),
                sellerName = sellerName.trim(),
                price = priceValue,
                contactNumber = contact.trim(),
                nequiAccount = nequi.trim(),
                daviplataAccount = daviplata.trim(),
                imagePath1 = imagePaths[0],
                imagePath2 = imagePaths[1],
                imagePath3 = imagePaths[2],
                imagePath4 = imagePaths[3],
            ),
        )
        toast("Producto guardado")
        onBack()
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 22.dp, top = 22.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Volver", tint = Lime, modifier = Modifier.size(18.dp))
                }
                Text("PUBLICAR PRODUCTO", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onBackground)
            }

            // Foto principal + acciones
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.3f).padding(top = 8.dp)) {
                ProductImage(path = imagePaths[0], seed = 0, modifier = Modifier.fillMaxSize())
                Row(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    PhotoActionButton(iconRes = R.drawable.ic_camera, label = "Cámara", filled = true, onClick = { openCamera() })
                    PhotoActionButton(iconRes = R.drawable.ic_gallery, label = "Galería", filled = false, onClick = { pickImage.launch("image/*") })
                }
            }

            // Miniaturas 1..4
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                imagePaths.forEachIndexed { index, path ->
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {
                        ProductImage(path = path, seed = index + 1, modifier = Modifier.fillMaxSize())
                        if (path.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(20.dp)
                                    .background(SurfaceHigh)
                                    .clickable { imagePaths[index] = "" },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(painterResource(R.drawable.ic_delete), contentDescription = "Borrar foto ${index + 1}", tint = Danger, modifier = Modifier.size(11.dp))
                            }
                        }
                    }
                }
            }
            Text(
                text = "${imagePaths.count { it.isNotBlank() }}/4 FOTOS",
                style = MaterialTheme.typography.labelSmall,
                color = OnBackgroundMuted,
                modifier = Modifier.padding(horizontal = 22.dp),
            )

            Column(modifier = Modifier.padding(22.dp)) {
                SectionLabel("INFORMACIÓN DEL EMPRENDIMIENTO")
                FormField(businessName, { businessName = it }, "Nombre del emprendimiento *")

                SectionDivider()

                SectionLabel("DETALLES DEL PRODUCTO")
                FormField(productName, { productName = it }, "Nombre del producto *")
                FormField(description, { description = it }, "Descripción / detalles", minLines = 3)
                FormField(sellerName, { sellerName = it }, "Nombre del vendedor")
                FormField(price, { price = it }, "Precio (COP) *", keyboardType = KeyboardType.Decimal)

                SectionDivider()

                SectionLabel("CONTACTO Y MÉTODOS DE PAGO")
                FormField(contact, { contact = it }, "# de contacto / WhatsApp", keyboardType = KeyboardType.Phone)
                FormField(nequi, { nequi = it }, "# Cuenta Nequi", keyboardType = KeyboardType.Phone, accent = Pink)
                FormField(daviplata, { daviplata = it }, "# Cuenta Daviplata", keyboardType = KeyboardType.Phone)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .height(54.dp)
                        .background(Lime)
                        .clickable { saveProduct() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("GUARDAR PRODUCTO", style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp), color = OnLime)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PhotoActionButton(iconRes: Int, label: String, filled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(0.dp))
            .background(if (filled) Lime else SurfaceHigh)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = if (filled) OnLime else Lime,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = if (filled) OnLime else MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = Lime,
        modifier = Modifier.padding(bottom = 12.dp, top = 4.dp),
    )
}

@Composable
private fun SectionDivider() {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp).height(1.dp).background(Outline))
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    accent: androidx.compose.ui.graphics.Color = Lime,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(0.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accent,
            unfocusedBorderColor = Outline,
            focusedLabelColor = accent,
            unfocusedLabelColor = OnBackgroundMuted,
            cursorColor = accent,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        ),
    )
}

private fun createImageFile(context: android.content.Context): File {
    val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val dir = context.getExternalFilesDir("Pictures")
    dir?.mkdirs()
    return File.createTempFile("PRODUCT_${stamp}_", ".jpg", dir)
}

private fun copyUriToAppPictures(context: android.content.Context, uri: Uri): String? {
    return try {
        val picturesDir = context.getExternalFilesDir("Pictures") ?: return null
        picturesDir.mkdirs()
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val outFile = File(picturesDir, "PRODUCT_GALLERY_$stamp.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            outFile.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        outFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
