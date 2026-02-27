package com.vendepro.app.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vendepro.app.data.model.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY uploadedAt DESC")
    fun getAllProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}
