package com.example.mylocation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mylocation.database.LocationEntity
import com.example.mylocation.database.LocationRoom
import com.example.mylocation.util.asDomain
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var listLatLng:List<LocationEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val database = LocationRoom.getDatabase(this)
        val scope  = CoroutineScope(Job() + Dispatchers.Main)

        scope.launch(Dispatchers.IO) {
            listLatLng = database.databaseDao().getLongLat()
        }

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val polyline = googleMap.addPolyline(PolylineOptions()
            .clickable(true)
            .add(*listLatLng.asDomain()))
        polyline.color = ContextCompat.getColor(this,R.color.colorPrimaryDark)
        polyline.endCap = RoundCap()

        val listLatLngSize = listLatLng.asDomain().size
        mMap.addMarker(MarkerOptions().position(listLatLng.asDomain()[0]).title("Starting point"))
        mMap.addMarker(MarkerOptions().position(listLatLng.asDomain()[listLatLngSize-1]).title("End zone"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listLatLng.asDomain()[0],15f))
    }

}