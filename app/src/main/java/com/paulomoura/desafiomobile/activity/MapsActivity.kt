package com.paulomoura.desafiomobile.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.paulomoura.desafiomobile.R
import com.paulomoura.desafiomobile.constant.AnalyticsConstants
import com.paulomoura.desafiomobile.data.dao.UserLocationDao
import com.paulomoura.desafiomobile.data.model.toUserLocation
import com.paulomoura.desafiomobile.databinding.ActivityMapsBinding
import com.paulomoura.desafiomobile.extension.bindings
import com.paulomoura.desafiomobile.service.LocationService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    @Inject
    lateinit var analytics: FirebaseAnalytics

    private val binding: ActivityMapsBinding by bindings(ActivityMapsBinding::inflate)
    private lateinit var googleMap: GoogleMap
    private val locationUpdateReceiver = LocationUpdateReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        registerReceiver(locationUpdateReceiver, IntentFilter(ACTION_LOCATION_UPDATE))
        startService(Intent(applicationContext, LocationService::class.java).apply { action = LocationService.ACTION_START })

        val fragmentMap = supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        fragmentMap.getMapAsync(this)
    }

    override fun onMapReady(readyMap: GoogleMap) {
        googleMap = readyMap
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-15.79, -47.88)))
        analytics.logEvent(AnalyticsConstants.EVENT_RENDERED_MAP) {
            param(AnalyticsConstants.PARAM_MAP_SUCCESS, true.toString())
        }
    }

    override fun onDestroy() {
        startService(Intent(applicationContext, LocationService::class.java).apply { action = LocationService.ACTION_STOP })
        unregisterReceiver(locationUpdateReceiver)
        super.onDestroy()
    }

    @AndroidEntryPoint
    inner class LocationUpdateReceiver : BroadcastReceiver() {

        @Inject
        lateinit var userLocationDao: UserLocationDao
        @Inject
        lateinit var auth: FirebaseAuth

        private var isStartingMarker = true
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_LOCATION_UPDATE) {
                val lat = intent.getDoubleExtra(LAT_EXTRA, 0.0)
                val long = intent.getDoubleExtra(LONG_EXTRA, 0.0)
                if (lat != 0.0 && long != 0.0) {
                    googleMap.clear()
                    val currentLocation = LatLng(lat, long)
                    googleMap.addMarker(MarkerOptions().position(currentLocation))
                    saveLastLocation(currentLocation)
                    if (isStartingMarker) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
                        isStartingMarker = false
                    }
                }
            }
        }

        private fun saveLastLocation(latLong: LatLng) {
            CoroutineScope(Dispatchers.Unconfined).launch {
                auth.currentUser?.let {
                    userLocationDao.insert(latLong.toUserLocation(it))
                    val userLocation = userLocationDao.getUserLocation(it.uid)
                    Log.d("ROOM", "Location saved locally: ${userLocation.toString()}")
                }
            }
        }
    }

    companion object {
        const val ACTION_LOCATION_UPDATE = "action_location_update"
        const val LAT_EXTRA = "lat"
        const val LONG_EXTRA = "long"
    }
}