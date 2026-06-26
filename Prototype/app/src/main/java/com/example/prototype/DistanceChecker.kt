package com.example.prototype

import android.content.Context
import android.os.Looper
import android.util.Log
import android.location.Location
import android.media.MediaPlayer
import android.os.Vibrator
import android.os.VibrationEffect
import com.example.prototype.LocationGetter
import kotlin.math.cos
import kotlin.math.sin


class DistanceChecker(private val context: Context) {
    var stopSuccsesCount = 0
    var stopPointsCount = 0
    var targetLatitude: Double? = null //目的地緯度
    var targetLongitude: Double? = null //目的地経度

    var nowLatitude: Double? = null//現在地緯度
    var nowLongtitude: Double? = null//現在地経度

    var nowSpeed = 100f

    val detectDistance = 30f //接近を検知する距離

    var previousDistance = -1f //

    var isChecks = true


    private var mediaPlayer: MediaPlayer? = null //メディアプレイヤー

    private var vibrator: Vibrator? = null //バイブレーション

    val pattern = longArrayOf(0, 500, 300, 500) //バイブレーションパターン

    //将来的に消す
    private val handler = android.os.Handler(Looper.getMainLooper())

//    private val chackRunnable = object : Runnable //繰り返し実行する処理

    //クラス生成時に自動実行
    init {
////        停止回数生成
//        stopSuccsesCount = 0
        //音声ファイル読み込み
        mediaPlayer = MediaPlayer.create(context,R.raw.alert_tsumugi)
        //ループ再生するように
        mediaPlayer?.isLooping = true

        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    }
//    private val checkRunnable = object : Runnable {
//    override
    fun run() {
        val results = FloatArray(1)
        targetLatitude ?: return
        targetLongitude ?: return
        nowLongtitude ?: return
        nowLatitude ?: return
        Location.distanceBetween(
            nowLatitude!!,
            nowLongtitude!!,
            targetLatitude!!,
            targetLongitude!!,
            results
        )
        val distance = results[0]
        val bearing = bearing()
        val radians = Math.toRadians(bearing.toDouble())
        val roadCheck = Math.abs(cos(radians) * distance)

        Log.d("DistanceCheck", "距離 = $distance")

        // 前回との比較
        if (previousDistance != -1f) {
            if (distance > previousDistance) {
                Log.d("DistanceCheck", "遠ざかっています")
                if (mediaPlayer?.isPlaying == true) {
                    //音声アラート停止
                    mediaPlayer?.pause()
                }
                //バイブレーション停止
                vibrator?.cancel()
//                isChecks = false
                //ここで次の一時停止地点を探すようにする
            }
        }
    Log.e("test","${roadCheck}")
        if (isChecks && distance <= detectDistance && roadCheck <= 5) {
            if (distance <= 5f){
                stopPointsCount++
                isChecks = false
                if (nowSpeed <= 5f){
                    stopSuccsesCount++
                    Log.e("test","一時停止")
                }
            }
            Log.d("DistanceCheck", "接近しました！")
            if (mediaPlayer?.isPlaying != true) {
                //音声ファイルを開始時に戻し、再生
                mediaPlayer?.seekTo(0)
                mediaPlayer?.start()
            }
            //バイブレーション開始 (androidのバージョンによってコードが違うので分岐)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 500, 300, 500),
                        0
                    )
                )
            } else {
                vibrator?.vibrate(
                    longArrayOf(0, 500, 300, 500),
                    0
                )
            }
        }

        previousDistance = distance



//            //将来的に消す
//            nowLongtitude += 0.0003
//            //nowLatitude += 0.0005

        //将来的に消す
//            if (isChecks) {
//                handler.postDelayed(this, 5000) //５秒後にまた呼び出す
//            }
//
    }

    fun bearing(): Float {
        // 目的地点
        val startLocation = Location("start").apply {
            latitude = targetLatitude!!
            longitude = targetLongitude!!
        }

        // 現在地点
        val endLocation = Location("end").apply {
            latitude = nowLatitude!!
            longitude = nowLongtitude!!
        }

        // bearingTo() で方位角を取得
        val bearing = startLocation.bearingTo(endLocation) //% 180

        return bearing
    }


    // 開始　将来的に消す
//    fun startChecking() {
//        handler.post(checkRunnable)
//    }
//
//    // 停止
//    fun stopChecking() {
//        handler.removeCallbacks(checkRunnable)
//    }
}

