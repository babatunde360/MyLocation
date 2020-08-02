package com.example.mylocation.util

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog

const val PERMISSION_ID = 234
const val REQUEST_CHECK_STATE = 12300

fun isLocationEnabled(context: Context):Boolean{
    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    var gpsEnabled = false
    var networkEnabled = false
    try {
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }catch (e : Exception){
        e.printStackTrace()
    }
    try {
        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }catch (e: Exception){
        e.printStackTrace()
    }
    if(!gpsEnabled && !networkEnabled){
        AlertDialog.Builder(context)
            .setMessage("GPS Enable")
            .setPositiveButton("Settings", DialogInterface.OnClickListener { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            })
            .setNegativeButton("Cancel",null)
            .show()
    }
    return gpsEnabled && networkEnabled
}