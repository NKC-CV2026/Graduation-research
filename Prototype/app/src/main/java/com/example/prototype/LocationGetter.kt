package com.example.prototype

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationGetter(
    private val context: Context,
) {
    private var lat: Double = 35.12093
    private var long: Double = 136.91254
    private var bearing: Float = 190f
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationCallback =

    object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations){
                Log.e(
                    "位置情報","緯度：${location.latitude}経度：${location.longitude}方位角：${location.bearing}"
                )
                lat = location.latitude
                long = location.longitude
                bearing = location.bearing
            }
        }
    }
    fun startLocationUpdate(){
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){
            Log.e("位置情報","権限が許可されてないよ")
            return
        }
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L
        ).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }
    fun stopLocationonUpdate(){
        fusedLocationClient.removeLocationUpdates(
            locationCallback
        )
    }
    fun getLat(): Double{
        return lat
    }
    fun getLong(): Double{
        return long
    }
    fun getBearing(): Float{
        return bearing
    }


}