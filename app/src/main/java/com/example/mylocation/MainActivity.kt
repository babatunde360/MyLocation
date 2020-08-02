package com.example.mylocation

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.mylocation.database.LocationEntity
import com.example.mylocation.database.LocationRoom
import com.example.mylocation.database.LocationRoom.Companion.getDatabase
import com.example.mylocation.util.REQUEST_CHECK_STATE
import com.example.mylocation.util.isLocationEnabled
import com.google.android.gms.location.*
import com.google.android.gms.tasks.RuntimeExecutionException
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : BaseActivity() {
    var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationUpdates:LocationCallback
    private lateinit var database: LocationRoom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        database = getDatabase(this)
        val scope  = CoroutineScope(Job() + Dispatchers.Main)


        recordLocationButton.setOnClickListener {
            if (isLocationEnabled(this)) {
                scope.launch { deleteDb()}

                val reqSetting = LocationRequest.create().apply {
                    fastestInterval = 1000
                    interval = 5000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    smallestDisplacement = 1.0f
                }
                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(reqSetting)

                val client = LocationServices.getSettingsClient(this)


                client.checkLocationSettings(builder.build()).addOnCompleteListener { _ ->
                    try {
                    } catch (e: RuntimeExecutionException) {
                        e.printStackTrace()
                    }

                }
                locationUpdates = object : LocationCallback() {
                    override fun onLocationResult(lr: LocationResult) {
                        scope.launch {
                            saveLongLat( lr.lastLocation.longitude,
                            lr.lastLocation.latitude)
                        }
                    }
                }

                fusedLocationClient?.requestLocationUpdates(reqSetting,
                    locationUpdates,
                    null)

                Toast.makeText(this,"Starts recording your location",
                    Toast.LENGTH_SHORT).show()

            }
        }

        stopLocationButton.setOnClickListener {
            if (this::locationUpdates.isInitialized){
              stopPeriodicLocation()
            startActivity(Intent(this,MapsActivity::class.java))
            }else{
                Toast.makeText(this,"showing your previously recorded location",
                    Toast.LENGTH_LONG).show()
                startActivity(Intent(this,MapsActivity::class.java))
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode and 0xFFFF == REQUEST_CHECK_STATE) {
            Log.e("LOG", "Back from REQUEST_CHECK_STATE")
        }
    }


private fun stopPeriodicLocation(){
    fusedLocationClient?.removeLocationUpdates(locationUpdates)
}

    suspend fun deleteDb(){
        withContext(Dispatchers.IO){
            database.databaseDao().deleteLongLat()
        }
    }

    suspend fun saveLongLat(longitude:Double,latitude: Double){
        withContext(Dispatchers.IO) {
            database.databaseDao().insertLongLat(
                LocationEntity(latitude,longitude)
            )
        }
    }
}