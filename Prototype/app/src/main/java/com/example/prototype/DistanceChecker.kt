package com.example.prototype

import android.Manifest
import android.content.Context
import android.os.Looper
import android.util.Log
import android.location.Location
import kotlinx.coroutines.Runnable
import java.util.logging.Handler
import android.media.MediaPlayer
import android.os.Vibrator
import android.os.VibrationEffect

class DistanceChecker(private val context: Context) {
    val targetLatitude = 35.600000 //目的地緯度
    val targetLongitude = 139.800000 //目的地経度

    var nowLatitude = 35.600000 //現在地緯度
    var nowLongtitude = 139.799000 //現在地経度

    val detectDistance = 80f //接近を検知する距離

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
        //音声ファイル読み込み
        mediaPlayer = MediaPlayer.create(context,R.raw.alert)
        //ループ再生するように
        mediaPlayer?.isLooping = true

        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    }
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
                    //音声アラート停止
                    mediaPlayer?.pause()
                    //バイブレーション停止
                    vibrator?.cancel()
                    isChecks = false
                    //ここで次の一時停止地点を探すようにする
                }
            }

            if (distance <= detectDistance && isChecks) {
                Log.d("DistanceCheck", "接近しました！")
                //音声ファイルを開始時に戻し、再生
                mediaPlayer?.seekTo(0)
                mediaPlayer?.start()
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

            //将来的に消す
            nowLongtitude += 0.0003
            //nowLatitude += 0.0005

            //将来的に消す
            if (isChecks) {
                handler.postDelayed(this, 5000) //５秒後にまた呼び出す
            }
        }
    }
    // 開始　将来的に消す
    fun startChecking() {
        handler.post(checkRunnable)
    }

    // 停止
    fun stopChecking() {
        handler.removeCallbacks(checkRunnable)
    }
}