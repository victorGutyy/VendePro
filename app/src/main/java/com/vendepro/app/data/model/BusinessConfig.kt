package com.vendepro.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_config")
data class BusinessConfig(
    @PrimaryKey val id: Int = 1,
    val businessName: String = "",
    val logoPath: String = "",
    val primaryColor: String = "#FF6B35",
    val sellerName: String = "",
    val contactNumber: String = "",
    val nequiAccount: String = "",
    val daviplataAccount: String = ""
)
