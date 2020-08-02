package com.example.mylocation.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface DatabaseDao {
    @Query("select * from longLat_table")
    fun getLongLat():List<LocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLongLat(locationEntity: LocationEntity)

    @Query("delete from longLat_table")
    fun deleteLongLat()
}