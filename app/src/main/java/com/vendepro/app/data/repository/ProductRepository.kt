package com.vendepro.app.data.repository

import com.vendepro.app.data.db.ProductDao
import com.vendepro.app.data.model.Product

class ProductRepository(private val dao: ProductDao) {
    val allProducts = dao.getAllProducts()

    suspend fun insert(product: Product) = dao.insertProduct(product)
    suspend fun update(product: Product) = dao.updateProduct(product)
    suspend fun delete(product: Product) = dao.deleteProduct(product)
    suspend fun getById(id: Int) = dao.getProductById(id)
}
