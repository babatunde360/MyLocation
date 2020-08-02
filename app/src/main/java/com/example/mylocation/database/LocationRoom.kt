package com.example.mylocation.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LocationEntity::class],version = 1,exportSchema = false)
abstract class LocationRoom : RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao

    companion object{
        @Volatile
        private var INSTANCE: LocationRoom?  = null

        fun getDatabase(context: Context):
                LocationRoom{
            var tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                var instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocationRoom::class.java,
                "locationRoomDatabase"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}