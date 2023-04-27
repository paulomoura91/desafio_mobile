package com.paulomoura.desafiomobile.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.paulomoura.desafiomobile.data.dao.UserLocationDao
import com.paulomoura.desafiomobile.data.model.UserLocation

@Database(entities = [UserLocation::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userLocationDao(): UserLocationDao
}