package com.example.mylocation

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.mylocation.util.PERMISSION_ID
import com.example.mylocation.util.REQUEST_CHECK_STATE
import com.example.mylocation.util.isLocationEnabled
import com.google.android.gms.location.*
import com.google.android.gms.tasks.RuntimeExecutionException
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationUpdates:LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        recordLocationButton.setOnClickListener {
            if (isLocationEnabled(this)) {
                val reqSetting = LocationRequest.create().apply {
                    fastestInterval = 1000
                    interval = 5000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    smallestDisplacement = 1.0f
                }

                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(reqSetting)

                val client = LocationServices.getSettingsClient(this)


                client.checkLocationSettings(builder.build()).addOnCompleteListener { task ->
                    try {
                    } catch (e: RuntimeExecutionException) {
                        e.printStackTrace()
                    }

                }
                locationUpdates = object : LocationCallback() {
                    override fun onLocationResult(lr: LocationResult) {
                        Toast.makeText(applicationContext,"$lr",Toast.LENGTH_SHORT).show()

                    }
                }

                fusedLocationClient?.requestLocationUpdates(reqSetting,
                    locationUpdates,
                    null)

            } else {
                currentLocation.text = "Permission not granted"
            }
        }

        stopLocationButton.setOnClickListener {
            isLocationEnabled(this)
            stopPeriodicLocation()
            Toast.makeText(this, isLocationEnabled(this).toString(),Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode and 0xFFFF == REQUEST_CHECK_STATE) {
            Log.e("LOG", "Back from REQUEST_CHECK_STATE")
            Toast.makeText(this, "Back from REQUEST_CHECK_STATE",Toast.LENGTH_SHORT).show()
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_ID -> {

            }
        }
    }

private fun stopPeriodicLocation(){
    fusedLocationClient?.removeLocationUpdates(locationUpdates)
}

}