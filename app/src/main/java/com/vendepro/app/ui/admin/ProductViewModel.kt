package com.vendepro.app.ui.admin

import android.app.Application
import androidx.lifecycle.*
import com.vendepro.app.data.db.VendeProDatabase
import com.vendepro.app.data.model.BusinessConfig
import com.vendepro.app.data.model.Product
import com.vendepro.app.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VendeProDatabase.getDatabase(application)
    private val repo = ProductRepository(db.productDao())
    private val configDao = db.businessConfigDao()

    val products: LiveData<List<Product>> = repo.allProducts
    val businessConfig: LiveData<BusinessConfig?> = configDao.getConfig()

    fun insert(product: Product) = viewModelScope.launch { repo.insert(product) }
    fun update(product: Product) = viewModelScope.launch { repo.update(product) }
    fun delete(product: Product) = viewModelScope.launch { repo.delete(product) }

    fun saveConfig(config: BusinessConfig) = viewModelScope.launch {
        configDao.saveConfig(config)
    }
}
