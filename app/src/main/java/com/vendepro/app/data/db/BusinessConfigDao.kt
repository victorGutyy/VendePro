package com.vendepro.app.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vendepro.app.data.model.BusinessConfig

@Dao
interface BusinessConfigDao {
    @Query("SELECT * FROM business_config WHERE id = 1")
    fun getConfig(): LiveData<BusinessConfig?>

    @Query("SELECT * FROM business_config WHERE id = 1")
    suspend fun getConfigOnce(): BusinessConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: BusinessConfig)
}
