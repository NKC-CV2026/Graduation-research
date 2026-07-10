package com.example.prototype

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.health.connect.datatypes.ExerciseRoute
import android.location.Location
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
import androidx.core.content.edit

// アプリを閉じても位置情報監視を継続するため
// バックグラウンドで位置情報を取得
// 一時停止標識の検知を行う
class ForegroundService : Service() {
    // 位置情報取得クラス
    private lateinit var locationGetter: LocationGetter
    // 一時停止標識検索クラス
    private lateinit var nearStopSign: NearStopSign
    // 距離判定・通知クラス
    private lateinit var distanceChecker: DistanceChecker
    // 前回検知した標識のuniqueKey (同じ標識への再通知を防ぐために使用する)
    var lastUniqueKey = ""

    private var lastUpdateLatitude: Double? = null
    private var lastUpdateLongitude: Double? = null

    // バックグラウンド処理用のCoroutine
    private val serviceScope = CoroutineScope(
        Dispatchers.Default + SupervisorJob()
    )

    private var monitoringJob: Job? = null

    override fun onCreate() {
        super.onCreate()

        // 各クラスの生成
        locationGetter = LocationGetter(this)
        nearStopSign = NearStopSign()
        distanceChecker = DistanceChecker(this)

        // JSONから一時停止標識データを読み込む
//        nearStopSign.setStopPoints(this)

        // 通知チャンネル作成
        createNotificationChannel()

        // フォアグラウンドサービス用通知
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

    // サービス開始時に呼ばれる
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        // 既に監視中なら新しく開始しない
        if (monitoringJob?.isActive == true) {
            Log.d("ForegroundService", "すでに監視中です")
            return START_STICKY
        }
        // GPSによる位置情報取得開始
        locationGetter.startLocationUpdate()

        // 位置情報取得と標識判定を繰り返す
        serviceScope.launch {
            while (isActive) {
                // 現在地情報を取得
                val nowLat = locationGetter.getLat()
                val nowLong = locationGetter.getLong()
                val nowBearing = locationGetter.getBearing()
                // 位置情報がまだ取得できていない場合は待機
                if (
                    nowLat == null ||
                    nowLong == null ||
                    nowBearing == null
                ) {
                    delay(1000L)
                    continue
                }
                if (lastUpdateLatitude == null || lastUpdateLongitude == null){
                    nearStopSign.setStopPoints(this@ForegroundService,nowLat,nowLong)
                    lastUpdateLatitude = nowLat
                    lastUpdateLongitude = nowLong
                }else{
                    val results = FloatArray(1)

                    Location.distanceBetween(
                        lastUpdateLatitude!!
                        ,lastUpdateLongitude!!
                        ,nowLat
                        ,nowLong
                        ,results
                    )

                    if (results[0] >= 50f){
                        nearStopSign.setStopPoints(this@ForegroundService,nowLat,nowLong)
                        lastUpdateLatitude = nowLat
                        lastUpdateLongitude = nowLong
                    }
                }
                // 現在地・進行方向から最も適切な一時停止標識を検索
                val nearStop = nearStopSign.matchStopSing(
                    nowLat,
                    nowLong,
                    nowBearing,
                    nearStopSign.stopPoints
                )
                // 対象標識が見つからなかった場合は次のループへ
                if (nearStop == null) {
                    delay(1000L)
                    continue
                }
                // 現在対象になっている標識のuniqueKeyを表示
                Log.e("test","${nearStop["uniqueKey"]}")
                // 前回と違う標識を検知した場合
                if (lastUniqueKey != nearStop["uniqueKey"].toString()){
                    distanceChecker.isChecks = true     // 新しい標識として判定を有効化
                    distanceChecker.previousDistance = -1f      // 前回距離を初期化
                    lastUniqueKey = nearStop["uniqueKey"].toString()    // 今回の標識を保存
                }
                // DistanceCheckerへ標識情報を渡す
                distanceChecker.targetLatitude = nearStop["lat"] as Double
                distanceChecker.targetLongitude = nearStop["long"] as Double
                distanceChecker.targetUniqueKey = nearStop["uniqueKey"].toString()
                // DistanceCheckerへ現在地情報を渡す
                distanceChecker.nowLatitude = nowLat
                distanceChecker.nowLongtitude = nowLong
                distanceChecker.nowSpeed = locationGetter.getSpeed()

//                distanceChecker.isChecks = true

                // 標識との距離判定を実行
                distanceChecker.run()

                delay(1000L)
            }
        }
        return START_STICKY
    }

    // サービス終了時にレポートデータを保存する
    override fun onDestroy() {
        super.onDestroy()
        // SharedPreferencesへ結果を保存
        val prefs = getSharedPreferences(
            "report"
            ,MODE_PRIVATE
        )
        // レポート表示用に、一時停止結果を端末内へ保存する
        prefs.edit {
            putInt(
                "STOP_SUCCSES_COUNT", distanceChecker.stopSuccsesCount
            )
            putInt(
                "STOP_POINTS_COUNT", distanceChecker.stopPointsCount
            )
        }
        Log.e("test","${distanceChecker.unStopSign}")
        // GPS取得停止
        locationGetter.stopLocationonUpdate()
        // Coroutine停止
        serviceScope.cancel()
    }

    // バインド型サービスではないため使用しない
    // Activityから直接通信するサービスではないので使っていない
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // Android 8.0以降で必要な通知チャンネルを作成する
    // (Android8以降はフォアグラウンドサービスを使うために通知チャンネルが必須)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // フォアグラウンドサービス用の通知チャンネルを作成
            val channel = NotificationChannel(
                CHANNEL_ID,
                "一時停止監視",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // システムの通知管理サービスを取得
            val manager = getSystemService(
                NotificationManager::class.java
            )
            // 通知チャンネルを登録
            manager.createNotificationChannel(
                channel
            )
        }
    }

    // 通知チャンネルIDと通知IDの定数 (通知で使う固定値を定数として管理)
    companion object {
        // 通知チャンネル識別用ID
        private const val CHANNEL_ID =
            "stop_sign_channel"
        // フォアグラウンド通知のID
        private const val NOTIFICATION_ID =
            1
    }
}

