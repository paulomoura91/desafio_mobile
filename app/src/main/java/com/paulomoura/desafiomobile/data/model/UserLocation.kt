package com.paulomoura.desafiomobile.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser

@Entity
data class UserLocation(
    @PrimaryKey val id: String,
    val email: String?,
    val lastLat: Double,
    val lastLong: Double
)

fun LatLng.toUserLocation(firebaseUser: FirebaseUser) = UserLocation(
    id = firebaseUser.uid,
    email = firebaseUser.email,
    lastLat = latitude,
    lastLong = longitude
)