package com.vendepro.app.ui.admin

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import java.util.*

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private lateinit var viewModel: ProductViewModel
    private var currentPhotoPath: String = ""

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openCamera() else toast("Permiso de cámara denegado")
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && currentPhotoPath.isNotEmpty())
                binding.ivProductPhoto.setImageURI(Uri.fromFile(File(currentPhotoPath)))
        }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                currentPhotoPath = getRealPathFromUri(it) ?: ""
                binding.ivProductPhoto.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                openCamera()
            else requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
        binding.btnGallery.setOnClickListener { pickImage.launch("image/*") }
        binding.btnSaveProduct.setOnClickListener { saveProduct() }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun openCamera() {
        val file = createImageFile()
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        takePicture.launch(uri)
    }

    private fun createImageFile(): File {
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File.createTempFile("PRODUCT_${stamp}_", ".jpg", getExternalFilesDir("Pictures"))
            .also { currentPhotoPath = it.absolutePath }
    }

    private fun saveProduct() {
        val businessName = binding.etBusinessName.text.toString().trim()
        val productName  = binding.etProductName.text.toString().trim()
        val priceText    = binding.etPrice.text.toString().trim()

        if (businessName.isEmpty() || productName.isEmpty() || priceText.isEmpty()) {
            toast("Completa los campos obligatorios: Emprendimiento, Producto y Precio")
            return
        }

        viewModel.insert(Product(
            businessName     = businessName,
            productName      = productName,
            description      = binding.etDescription.text.toString().trim(),
            sellerName       = binding.etSellerName.text.toString().trim(),
            price            = priceText.toDoubleOrNull() ?: 0.0,
            contactNumber    = binding.etContact.text.toString().trim(),
            nequiAccount     = binding.etNequi.text.toString().trim(),
            daviplataAccount = binding.etDaviplata.text.toString().trim(),
            imagePath        = currentPhotoPath
        ))
        toast("✅ Producto guardado")
        finish()
    }

    private fun getRealPathFromUri(uri: Uri): String? {
        var result: String? = null
        contentResolver.query(uri, null, null, null, null)?.use {
            if (it.moveToFirst()) {
                val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                if (idx >= 0) result = it.getString(idx)
            }
        }
        return result
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
