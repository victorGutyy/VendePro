package com.vendepro.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vendepro.app.data.db.VendeProDatabase
import com.vendepro.app.data.model.BusinessConfig
import com.vendepro.app.data.model.Product
import com.vendepro.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VendeProViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VendeProDatabase.getDatabase(application)
    private val repo = ProductRepository(db.productDao())
    private val configDao = db.businessConfigDao()

    val products: StateFlow<List<Product>> = repo.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val businessConfig: StateFlow<BusinessConfig?> = configDao.getConfig()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun productById(id: Int): Flow<Product?> = repo.getById(id)

    fun insert(product: Product) = viewModelScope.launch { repo.insert(product) }
    fun update(product: Product) = viewModelScope.launch { repo.update(product) }
    fun delete(product: Product) = viewModelScope.launch { repo.delete(product) }

    fun saveConfig(config: BusinessConfig) = viewModelScope.launch {
        configDao.saveConfig(config)
    }
}
