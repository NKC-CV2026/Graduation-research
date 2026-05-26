package com.example.prototype

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.IpSecManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.os.postDelayed
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import android.location.Location
import kotlinx.coroutines.Runnable
import java.util.logging.Handler

class DistanceChecker {
    val targetLatitude = 35.600000 //目的地緯度
    val targetLongitude = 139.800000 //目的地経度

    var nowLatitude = 35.600000 //現在地緯度
    var nowLongtitude = 139.799000 //現在地経度

    val detectDistance = 10f //接近を検知する距離

    var previousDistance = -1f //

    var isChecks = true

    private val handler = android.os.Handler(Looper.getMainLooper())

//    private val chackRunnable = object : Runnable //繰り返し実行する処理

    private val checkRunnable = object : Runnable {
        override fun run() {
            val results = FloatArray(1)
            Location.distanceBetween(
                nowLatitude,
                nowLongtitude,
                targetLatitude,
                targetLongitude,
                results
            )
            val distance = results[0]

            Log.d("DistanceCheck", "距離 = $distance")

            // 前回との比較
            if (previousDistance != -1f) {
                if (distance > previousDistance) {
                    Log.d("DistanceCheck", "遠ざかっています")
                    isChecks = false
                    //ここで次の一時停止地点を探すようにする
                }
            }

            if (distance <= detectDistance) {
                Log.d("DistanceCheck", "接近しました！")
            }

            previousDistance = distance

            nowLongtitude += 0.0003
            //nowLatitude += 0.0005
            if (isChecks) {
                handler.postDelayed(this, 5000) //５秒後にまた呼び出す
            }
        }
    }
    // 開始
    fun startChecking() {
        handler.post(checkRunnable)
    }

    // 停止
    fun stopChecking() {
        handler.removeCallbacks(checkRunnable)
    }
}