package com.vendepro.app.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.vendepro.app.data.model.BusinessConfig
import com.vendepro.app.databinding.ActivityBusinessConfigBinding

class BusinessConfigActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBusinessConfigBinding
    private lateinit var viewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        viewModel.businessConfig.observe(this) { config ->
            config?.let {
                binding.etBusinessName.setText(it.businessName)
                binding.etSellerName.setText(it.sellerName)
                binding.etContact.setText(it.contactNumber)
                binding.etNequi.setText(it.nequiAccount)
                binding.etDaviplata.setText(it.daviplataAccount)
            }
        }

        binding.btnSave.setOnClickListener {
            viewModel.saveConfig(BusinessConfig(
                businessName     = binding.etBusinessName.text.toString().trim(),
                sellerName       = binding.etSellerName.text.toString().trim(),
                contactNumber    = binding.etContact.text.toString().trim(),
                nequiAccount     = binding.etNequi.text.toString().trim(),
                daviplataAccount = binding.etDaviplata.text.toString().trim()
            ))
            Toast.makeText(this, "✅ Configuración guardada", Toast.LENGTH_SHORT).show()
            finish()
        }
        binding.btnBack.setOnClickListener { finish() }
    }
}
