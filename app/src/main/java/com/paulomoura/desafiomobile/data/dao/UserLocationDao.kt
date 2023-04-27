package com.paulomoura.desafiomobile.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paulomoura.desafiomobile.data.model.UserLocation

@Dao
interface UserLocationDao {

    @Query("SELECT * FROM userlocation WHERE id = :id")
    suspend fun getUserLocation(id: String): UserLocation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserLocation): Long
}