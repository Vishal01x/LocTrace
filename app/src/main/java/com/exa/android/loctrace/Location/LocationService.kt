package com.exa.android.loctrace.Location

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.exa.android.loctrace.Model.Location
import com.exa.android.loctrace.R
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient : LocationClient
    private lateinit var database : DatabaseReference
    private lateinit var userId : String

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        database = FirebaseDatabase.getInstance().reference
        userId = "Vishal"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(){
        val notification = NotificationCompat.Builder(this,"location")
            .setContentTitle("Tracking location...")
            .setContentText("Location : null")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)  // user can't swipe it

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(2000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude
                val long = location.latitude
                val updateNotification = notification.setContentText(
                    "Location : ($lat, $long)"
                )
                Log.d("LocationService", "lat : ${lat}, long : ${long}")
                saveLocationInDatabase(lat,long)
                notificationManager.notify(1,updateNotification.build())
            }
            .launchIn(serviceScope)
        startForeground(1,notification.build())
    }

    fun saveLocationInDatabase(latitude : Double, longitude : Double){
        val location = Location(latitude,longitude)
        database.child("users").child(userId).child("locations").push().setValue(location)
            .addOnSuccessListener {
                Log.d("LocationService", "Location saved to Realtime Database")
            }
            .addOnFailureListener { e ->
                Log.e("Location Service", "Error", e)
            }
    }

    private fun stop(){
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}