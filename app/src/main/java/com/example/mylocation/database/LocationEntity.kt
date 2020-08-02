package com.example.mylocation.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "longLat_table")
data class LocationEntity(
    @ColumnInfo(name="longitude")
    val longitude: Double,
    @ColumnInfo(name = "latitude")
    var latitude: Double,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)