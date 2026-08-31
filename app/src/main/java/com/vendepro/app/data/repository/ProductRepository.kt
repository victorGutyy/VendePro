package com.vendepro.app.data.repository

import com.vendepro.app.data.db.ProductDao
import com.vendepro.app.data.model.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val dao: ProductDao) {
    val allProducts: Flow<List<Product>> = dao.getAllProducts()

    fun getById(id: Int): Flow<Product?> = dao.getProductById(id)

    suspend fun insert(product: Product) = dao.insertProduct(product)
    suspend fun update(product: Product) = dao.updateProduct(product)
    suspend fun delete(product: Product) = dao.deleteProduct(product)
}
