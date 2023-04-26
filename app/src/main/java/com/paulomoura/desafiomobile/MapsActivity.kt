package com.paulomoura.desafiomobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.paulomoura.desafiomobile.databinding.ActivityMapsBinding
import com.paulomoura.desafiomobile.extension.bindings

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val binding: ActivityMapsBinding by bindings(ActivityMapsBinding::inflate)
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val fragmentMap = supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        fragmentMap.getMapAsync(this)
    }

    override fun onMapReady(readyMap: GoogleMap) {
        googleMap = readyMap
        val saoPaulo = LatLng(-23.5489, -46.6388 )
        googleMap.addMarker(MarkerOptions().position(saoPaulo))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saoPaulo, 12f))
    }
}