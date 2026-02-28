package com.vendepro.app.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val businessName: String,
    val productName: String,
    val description: String,
    val sellerName: String,
    val price: Double,
    val contactNumber: String,
    val nequiAccount: String,
    val daviplataAccount: String,
    // 4 fotos
    val imagePath1: String,
    val imagePath2: String = "",
    val imagePath3: String = "",
    val imagePath4: String = "",

    val uploadedAt: Long = System.currentTimeMillis()
) : Parcelable
