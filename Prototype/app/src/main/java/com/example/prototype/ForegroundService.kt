package com.example.prototype

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.*


class ForegroundService : Service() {
    private lateinit var locationGetter: LocationGetter
    private lateinit var nearStopSign: NearStopSign
    private lateinit var distanceChecker: DistanceChecker

    private var lastUniqueKey = 0

    private val serviceScope = CoroutineScope(
        Dispatchers.Default + SupervisorJob()
    )

    private var monitoringJob: Job? = null

    override fun onCreate() {
        super.onCreate()

        locationGetter = LocationGetter(this)
        nearStopSign = NearStopSign()
        distanceChecker = DistanceChecker(this)

        nearStopSign.setStopPoints(this)

        createNotificationChannel()

        val notification: Notification =
            NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )
                .setContentTitle("一時停止監視中")
                .setContentText("バックグラウンドで監視しています")
                .setSmallIcon(R.drawable.test)
                .build()

        startForeground(
            NOTIFICATION_ID,
            notification
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (monitoringJob?.isActive == true) {
            Log.d("ForegroundService", "すでに監視中です")
            return START_STICKY
        }
        locationGetter.startLocationUpdate()

        serviceScope.launch {

            while (isActive) {
                val nowLat = locationGetter.getLat()
                val nowLong = locationGetter.getLong()
                val nowBearing = locationGetter.getBearing()
                
                if (
                    nowLat == null ||
                    nowLong == null ||
                    nowBearing == null
                ) {
                    delay(1000)
                    continue
                }
                val nearStop = nearStopSign.matchStopSing(
                    nowLat,
                    nowLong,
                    nowBearing,
                    nearStopSign.stopPoints
                )

                if (nearStop == null) {
                    delay(1000L)
                    continue
                }
                if (nearStop["uniqueKey"] != lastUniqueKey){
                    distanceChecker.isChecks = true
                    lastUniqueKey = nearStop["uniqueKey"].toString().toInt()
                }

                distanceChecker.targetLatitude = nearStop["lat"] as Double
                distanceChecker.targetLongitude = nearStop["long"] as Double

                distanceChecker.nowLatitude = nowLat
                distanceChecker.nowLongtitude = nowLong
                distanceChecker.nowSpeed = locationGetter.getSpeed()

//                distanceChecker.isChecks = true
                distanceChecker.run()

                delay(1000L)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        val prefs = getSharedPreferences(
            "report"
            ,MODE_PRIVATE
        )
        prefs.edit()
            .putInt(
                "STOP_SUCCSES_COUNT"
                ,distanceChecker.stopSuccsesCount
            )
        prefs.edit()
            .putInt(
                "STOP_POINTS_COUNT"
                ,distanceChecker.stopPointsCount
            ).apply()

        locationGetter.stopLocationonUpdate()

        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "一時停止監視",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(
                NotificationManager::class.java
            )

            manager.createNotificationChannel(
                channel
            )
        }
    }

    companion object {
        private const val CHANNEL_ID =
            "stop_sign_channel"

        private const val NOTIFICATION_ID =
            1
    }
}

