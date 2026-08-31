package com.vendepro.app.ui.catalog

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.vendepro.app.databinding.ActivityCatalogBinding
import com.vendepro.app.ui.admin.AddProductActivity
import com.vendepro.app.ui.admin.BusinessConfigActivity
import com.vendepro.app.ui.admin.ProductViewModel
import com.vendepro.app.utils.ShareHelper

class CatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogBinding
    private lateinit var viewModel: ProductViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        adapter = ProductAdapter(
            onShare = { p ->
                val cardUri = ShareHelper.generateProductCard(this, p)
                if (cardUri != null) {
                    ShareHelper.shareGeneral(this, cardUri, ShareHelper.buildProductText(p))
                } else {
                    toast("No se pudo generar la imagen del producto")
                }
            },
            onDelete = { p ->
                confirmDelete(p)
            },
            onSharePersonal = { p ->
                ShareHelper.shareProductToClient(this, p)
            }
        )

        binding.rvProducts.layoutManager = GridLayoutManager(this, 2)
        binding.rvProducts.adapter = adapter

        viewModel.products.observe(this) {
            adapter.submitList(it)
            binding.tvProductCount.text = "${it.size} productos"
        }
        viewModel.businessConfig.observe(this) { config ->
            config?.let { binding.tvBusinessName.text = it.businessName }
        }

        binding.fabAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }
        binding.btnShareCatalog.setOnClickListener {
            withCatalogCard { uri -> ShareHelper.shareGeneral(this, uri, "Catálogo disponible") }
        }
        binding.btnShareWhatsApp.setOnClickListener {
            withCatalogCard { uri -> ShareHelper.shareToWhatsApp(this, uri, "🛍️ Catálogo") }
        }
        binding.btnShareInstagram.setOnClickListener {
            withCatalogCard { uri -> ShareHelper.shareToInstagram(this, uri) }
        }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, BusinessConfigActivity::class.java))
        }
    }

    private fun withCatalogCard(onReady: (android.net.Uri) -> Unit) {
        val prods = viewModel.products.value
        if (prods.isNullOrEmpty()) {
            toast("Agrega al menos un producto para compartir el catálogo")
            return
        }
        val uri = ShareHelper.generateCatalogCard(this, prods)
        if (uri != null) onReady(uri) else toast("No se pudo generar el catálogo")
    }

    private fun confirmDelete(p: com.vendepro.app.data.model.Product) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage("¿Seguro que quieres eliminar \"${p.productName}\"? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ -> viewModel.delete(p) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}