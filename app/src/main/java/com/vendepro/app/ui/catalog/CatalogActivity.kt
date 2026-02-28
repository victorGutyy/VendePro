package com.vendepro.app.ui.catalog

import android.content.Intent
import android.os.Bundle
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
                ShareHelper.generateProductCard(this, p)?.let { cardUri ->
                    ShareHelper.shareGeneral(this, cardUri, ShareHelper.buildProductText(p))
                }
            },
            onDelete = { p ->
                viewModel.delete(p)
            },
            onSharePersonal = { p ->
                ShareHelper.shareProductToClient(this, p)
            }
        ) // ✅ ESTE PARÉNTESIS TE FALTABA

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
            val prods = viewModel.products.value ?: return@setOnClickListener
            ShareHelper.generateCatalogCard(this, prods)
                ?.let { ShareHelper.shareGeneral(this, it, "Catálogo disponible") }
        }
        binding.btnShareWhatsApp.setOnClickListener {
            val prods = viewModel.products.value ?: return@setOnClickListener
            ShareHelper.generateCatalogCard(this, prods)
                ?.let { ShareHelper.shareToWhatsApp(this, it, "🛍️ Catálogo") }
        }
        binding.btnShareInstagram.setOnClickListener {
            val prods = viewModel.products.value ?: return@setOnClickListener
            ShareHelper.generateCatalogCard(this, prods)
                ?.let { ShareHelper.shareToInstagram(this, it) }
        }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, BusinessConfigActivity::class.java))
        }
    }
}