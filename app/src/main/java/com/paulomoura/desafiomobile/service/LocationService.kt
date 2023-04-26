package com.paulomoura.desafiomobile.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.paulomoura.desafiomobile.LocationClient
import com.paulomoura.desafiomobile.MapsActivity.Companion.ACTION_LOCATION_UPDATE
import com.paulomoura.desafiomobile.MapsActivity.Companion.LAT_EXTRA
import com.paulomoura.desafiomobile.MapsActivity.Companion.LONG_EXTRA
import com.paulomoura.desafiomobile.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val locationClient by lazy { LocationClient(applicationContext, LocationServices.getFusedLocationProviderClient(applicationContext)) }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_LOCATION)
            .setContentTitle("Buscando localização...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates(10000L)
            .catch { Firebase.crashlytics.recordException(it) }
            .onEach { location ->
                notificationManager.notify(1, notification.build())
                updateMapLocation(location.latitude, location.longitude)
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun updateMapLocation(lat: Double, long: Double) {
        val latLongIntent = Intent().apply {
            action = ACTION_LOCATION_UPDATE
            putExtra(LAT_EXTRA, lat)
            putExtra(LONG_EXTRA, long)
        }
        sendBroadcast(latLongIntent)
    }

    private fun stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "action_start"
        const val ACTION_STOP = "action_stop"
        const val NOTIFICATION_CHANNEL_LOCATION = "location"
    }
}