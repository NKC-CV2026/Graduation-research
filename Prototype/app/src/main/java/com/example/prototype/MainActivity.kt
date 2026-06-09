package com.example.prototype

import android.Manifest
import android.content.pm.PackageManager
//import android.health.connect.datatypes.ExerciseRoute
import android.os.Bundle
//import android.location.Location
//import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.prototype.databinding.ActivityMainBinding
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationCallback
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationResult
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.location.Priority
//import com.example.prototype.LocationGetter
//import com.example.prototype.NearStopSign
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationGetter: LocationGetter
    private lateinit var nearStopSign: NearStopSign
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private var isMonitoring = false
    private lateinit var distanceChecker: DistanceChecker
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationGetter = LocationGetter(this)
        // DistanceChecker生成
        distanceChecker = DistanceChecker(this)
        nearStopSign = NearStopSign()
//        nearStopSign.setStopPoints(this)
        binding.locationBtn.setOnClickListener() {
            isMonitoring = !isMonitoring
            if (isMonitoring){
                distanceChecker.stopSuccsesCount = 0
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.e("位置情報", "権限が許可されてないよ")
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                    isMonitoring = false
                    return@setOnClickListener
                }
                locationGetter.startLocationUpdate()
                binding.locationBtn.text = getString(R.string.btn_onLocation)
                lifecycleScope.launch {
                    while (isMonitoring) {
                        //近くの一時停止標識の配列の設定
                        nearStopSign.setStopPoints(this@MainActivity)
//                        Log.e("配列の確認","${nearStopSign.stopPoints}")
                        var nowLat = locationGetter.getLat()
                        var nowLong = locationGetter.getLong()
                        var nowBearing = locationGetter.getBearing()
                        if (nowLat == null || nowLong == null || nowBearing == null) {
                            Log.e("現在地なし","現在地なし")
                            delay(1000L)
                            continue
                        }
                        //一時停止の特定
                        val nearrestStop = nearStopSign.matchStopSing(
                            nowLat,
                            nowLong,
                            nowBearing,
                            nearStopSign.stopPoints
                        )

                        if (nearrestStop == null) {
                            Log.e("一時停止なし","一時停止なし")
                            delay(1000L)
                            continue
                        }

                        //最寄りの一時停止の値設定
                        distanceChecker.targetLatitude = nearrestStop["lat"] as Double
                        distanceChecker.targetLongitude = nearrestStop["long"] as Double
                        distanceChecker.isChecks = true // 地点特定完了通知
                        //現在地の値設定
                        distanceChecker.nowLatitude = locationGetter.getLat()
                        distanceChecker.nowLongtitude = locationGetter.getLong()
//                       速度受け渡し
                        distanceChecker.nowSpeed = locationGetter.getSpeed()
                        Log.e("test","${locationGetter.getSpeed()}")
//                        距離判定実行
                        distanceChecker.run()


                        delay(1000L)
//unrecoverably broken and will be disposed!
                        //ボタン押して少ししたら出るエラークラッシュする
                    }
                }
                // 距離判定実行開始
//                distanceChecker.startChecking()
            }else{
                locationGetter.stopLocationonUpdate()
                binding.locationBtn.text = getString(R.string.btn_offLocation)
                Log.e("test","${distanceChecker.stopSuccsesCount}")
                // 距離判定停止
//                distanceChecker.stopChecking()
            }

        }



    }

    override fun onDestroy() {
        super.onDestroy()
        locationGetter.stopLocationonUpdate()
        // 停止
//        distanceChecker.stopChecking()
    }
}

