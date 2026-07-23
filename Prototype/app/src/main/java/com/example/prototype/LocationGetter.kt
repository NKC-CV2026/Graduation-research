package com.example.prototype

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

// GPSから現在地情報を取得
// 他のクラスへ現在地・方位角・速度を渡すクラス
class LocationGetter(
    private val context: Context,
) {
    private var lat: Double? = null     // 現在地の緯度
    private var long: Double? = null    // 現在地の経度
    private var preLat: Double? = null     // 前回の緯度
    private var preLong: Double? = null    // 前回の経度
    private var bearing: Float? = null     // 端末の進行方向

    private var speed: Float = 5f    // 現在の移動速度

    // Google Play Servicesの位置情報取得機能
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // 位置情報更新時に呼び出されるコールバック
    private val locationCallback =
        object : LocationCallback() {
            // 新しい位置情報を受信した時に実行される
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {

                    // 取得した位置情報を保存する
                    preLat = lat
                    lat = location.latitude
                    preLong = long
                    long = location.longitude
                    bearing = if(preLat != null || preLong != null){
                        setBearing(preLat!!,preLong!!,lat!!,long!!)
                    }else{
                        location.bearing
                    }
                    speed = location.speed
                    // 緯度・経度・方位角・速度を表示する
                    Log.e(
                        "位置情報",
                        "緯度：${location.latitude}経度：${location.longitude}方位角：${location.bearing}速度:${location.speed}"
                    )
                }
            }
        }

    // GPS位置情報取得開始
    fun startLocationUpdate() {
        // 位置情報権限が無い場合は処理しない
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("位置情報", "権限が許可されてないよ")
            return
        }
        // 高精度GPSを1秒間隔で取得する設定
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L
        ).build()

        // 位置情報取得開始
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    // GPS位置情報取得停止
    fun stopLocationonUpdate() {
        // コールバックを解除して位置情報取得を停止する
        fusedLocationClient.removeLocationUpdates(
            locationCallback
        )
    }

    // 現在の緯度を返す
    fun getLat(): Double? {
        return lat
    }

    // 現在の経度を返す
    fun getLong(): Double? {
        return long
    }

    // 現在の方位角を返す
    fun getBearing(): Float? {
        return bearing
    }

    // 現在の速度を返す
    fun getSpeed(): Float {
        return speed
    }

    fun setBearing(preLat: Double,preLong: Double,nowLat: Double,nowLong: Double): Float{
        val results = FloatArray(3)
        // 現在地から標識までの距離と方位角を求める
        Location.distanceBetween(
            preLat,
            preLong,
            nowLat,
            nowLong,
            results
        )
        return results[1]
    }
}