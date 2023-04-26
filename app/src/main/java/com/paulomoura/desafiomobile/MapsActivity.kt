package com.paulomoura.desafiomobile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.paulomoura.desafiomobile.databinding.ActivityMapsBinding
import com.paulomoura.desafiomobile.extension.bindings
import com.paulomoura.desafiomobile.service.LocationService


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

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
    }

    override fun onDestroy() {
        startService(Intent(applicationContext, LocationService::class.java).apply { action = LocationService.ACTION_STOP })
        unregisterReceiver(locationUpdateReceiver)
        super.onDestroy()
    }

    inner class LocationUpdateReceiver : BroadcastReceiver() {
        private var isStartingMarker = true
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_LOCATION_UPDATE) {
                val lat = intent.getDoubleExtra(LAT_EXTRA, 0.0)
                val long = intent.getDoubleExtra(LONG_EXTRA, 0.0)
                if (lat != 0.0 && long != 0.0) {
                    googleMap.clear()
                    val currentLocation = LatLng(lat, long)
                    googleMap.addMarker(MarkerOptions().position(currentLocation))
                    if (isStartingMarker) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
                        isStartingMarker = false
                        //colocar loading até ter primeira localizacao
                    }
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