package com.vendepro.app.ui.admin

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.vendepro.app.data.model.Product
import com.vendepro.app.databinding.ActivityAddProductBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private lateinit var viewModel: ProductViewModel

    // Slots de 4 fotos
    private val imagePaths = MutableList(4) { "" }   // [0..3]
    private var pendingSlot = -1
    private var pendingCameraPath: String = ""

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openCamera() else toast("Permiso de cámara denegado")
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && pendingSlot >= 0 && pendingCameraPath.isNotEmpty()) {
                imagePaths[pendingSlot] = pendingCameraPath
                showPreviewAndThumbs(pendingSlot)
            }
            pendingSlot = -1
            pendingCameraPath = ""
        }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val slot = nextFreeSlot()
                if (slot == -1) {
                    toast("Máximo 4 fotos")
                    return@registerForActivityResult
                }

                val path = copyUriToAppPictures(it)
                if (path.isNullOrEmpty()) {
                    toast("No se pudo guardar la imagen")
                    return@registerForActivityResult
                }

                imagePaths[slot] = path
                showPreviewAndThumbs(slot)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        // Carga datos de config si existen
        viewModel.businessConfig.observe(this) { config ->
            config?.let {
                if (binding.etBusinessName.text.isNullOrEmpty()) binding.etBusinessName.setText(it.businessName)
                if (binding.etSellerName.text.isNullOrEmpty()) binding.etSellerName.setText(it.sellerName)
                if (binding.etContact.text.isNullOrEmpty()) binding.etContact.setText(it.contactNumber)
                if (binding.etNequi.text.isNullOrEmpty()) binding.etNequi.setText(it.nequiAccount)
                if (binding.etDaviplata.text.isNullOrEmpty()) binding.etDaviplata.setText(it.daviplataAccount)
            }
        }

        binding.btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnGallery.setOnClickListener { pickImage.launch("image/*") }

        binding.btnSaveProduct.setOnClickListener { saveProduct() }

        binding.btnBack.setOnClickListener { finish() }

        // Borrar slots
        binding.btnDel1.setOnClickListener { deleteSlot(0) }
        binding.btnDel2.setOnClickListener { deleteSlot(1) }
        binding.btnDel3.setOnClickListener { deleteSlot(2) }
        binding.btnDel4.setOnClickListener { deleteSlot(3) }

        // Inicializa UI
        showPreviewAndThumbs(0)
    }

    private fun nextFreeSlot(): Int = imagePaths.indexOfFirst { it.isBlank() }

    private fun openCamera() {
        val slot = nextFreeSlot()
        if (slot == -1) {
            toast("Máximo 4 fotos")
            return
        }

        val file = createImageFile()
        pendingSlot = slot
        pendingCameraPath = file.absolutePath

        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        takePicture.launch(uri)
    }

    private fun createImageFile(): File {
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val dir = getExternalFilesDir("Pictures")
        dir?.mkdirs()
        return File.createTempFile("PRODUCT_${stamp}_", ".jpg", dir)
    }

    // Copia imagen de galería a la carpeta de tu app (para tener path real)
    private fun copyUriToAppPictures(uri: Uri): String? {
        return try {
            val picturesDir = getExternalFilesDir("Pictures") ?: return null
            picturesDir.mkdirs()

            val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val outFile = File(picturesDir, "PRODUCT_GALLERY_${stamp}.jpg")

            contentResolver.openInputStream(uri)?.use { input ->
                outFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return null

            outFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun deleteSlot(index: Int) {
        if (index !in 0..3) return
        imagePaths[index] = ""

        val nextPreview = imagePaths.indexOfFirst { it.isNotBlank() }
        if (nextPreview >= 0) {
            showPreviewAndThumbs(nextPreview)
        } else {
            binding.ivProductPhoto.setImageDrawable(null)
            showPreviewAndThumbs(0)
        }
    }

    private fun showPreviewAndThumbs(previewSlot: Int) {
        val previewPath = imagePaths.getOrNull(previewSlot).orEmpty()
        if (previewPath.isNotBlank()) {
            binding.ivProductPhoto.setImageURI(Uri.fromFile(File(previewPath)))
        } else {
            // si no hay preview, no forzamos nada
        }

        fun setThumb(slot: Int, path: String) {
            val iv = when (slot) {
                0 -> binding.ivThumb1
                1 -> binding.ivThumb2
                2 -> binding.ivThumb3
                else -> binding.ivThumb4
            }
            val del = when (slot) {
                0 -> binding.btnDel1
                1 -> binding.btnDel2
                2 -> binding.btnDel3
                else -> binding.btnDel4
            }

            if (path.isBlank()) {
                iv.setImageDrawable(null)
                iv.setBackgroundColor(android.graphics.Color.parseColor("#1A1A2E"))
                del.visibility = android.view.View.GONE
            } else {
                iv.setImageURI(Uri.fromFile(File(path)))
                del.visibility = android.view.View.VISIBLE
            }
        }

        setThumb(0, imagePaths[0])
        setThumb(1, imagePaths[1])
        setThumb(2, imagePaths[2])
        setThumb(3, imagePaths[3])

        val count = imagePaths.count { it.isNotBlank() }
        binding.tvPhotoCount.text = "$count/4"
    }

    private fun saveProduct() {
        val businessName = binding.etBusinessName.text.toString().trim()
        val productName  = binding.etProductName.text.toString().trim()
        val priceText    = binding.etPrice.text.toString().trim()

        if (businessName.isEmpty() || productName.isEmpty() || priceText.isEmpty()) {
            toast("Completa los campos obligatorios: Emprendimiento, Producto y Precio")
            return
        }

        if (imagePaths[0].isBlank()) {
            toast("Agrega mínimo 1 foto (Foto 1)")
            return
        }

        viewModel.insert(
            Product(
                businessName     = businessName,
                productName      = productName,
                description      = binding.etDescription.text.toString().trim(),
                sellerName       = binding.etSellerName.text.toString().trim(),
                price            = priceText.toDoubleOrNull() ?: 0.0,
                contactNumber    = binding.etContact.text.toString().trim(),
                nequiAccount     = binding.etNequi.text.toString().trim(),
                daviplataAccount = binding.etDaviplata.text.toString().trim(),
                imagePath1       = imagePaths[0],
                imagePath2       = imagePaths[1],
                imagePath3       = imagePaths[2],
                imagePath4       = imagePaths[3]
            )
        )

        toast("✅ Producto guardado")
        finish()
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}