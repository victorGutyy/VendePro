package com.vendepro.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vendepro.app.data.model.BusinessConfig
import com.vendepro.app.data.model.Product

@Database(
    entities = [Product::class, BusinessConfig::class],
    version = 1,
    exportSchema = false
)
abstract class VendeProDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun businessConfigDao(): BusinessConfigDao

    companion object {
        @Volatile private var INSTANCE: VendeProDatabase? = null

        fun getDatabase(context: Context): VendeProDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VendeProDatabase::class.java,
                    "vendepro_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
