package com.example.prototype

import android.os.Bundle
import android.view.inputmethod.InputBinding
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prototype.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
<<<<<<< Updated upstream
        var isBackground = false
        binding.backgroundBtn.setOnClickListener {
            isBackground = !isBackground
            if(isBackground){
                binding.backgroundBtn.text = getString(R.string.btn_onBackground)
            }else{
                binding.backgroundBtn.text = getString(R.string.btn_offBackground)
=======
        //通知欄表示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                200
            )
        }

        locationGetter = LocationGetter(this)
        // DistanceChecker生成
        distanceChecker = DistanceChecker(this)
        nearStopSign = NearStopSign()
//        nearStopSign.setStopPoints(this)
        // 監視開始・停止ボタン
        binding.locationBtn.setOnClickListener() {
            // 監視状態を切り替える
            isMonitoring = !isMonitoring
            // 監視開始時の処理
            if (isMonitoring){
//                distanceChecker.stopSuccsesCount = 0
//                distanceChecker.stopPointsCount = 0
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.e("位置情報", "権限が許可されてないよ")
                    // 位置情報権限が無い場合は取得する
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
                //locationGetter.startLocationUpdate()
                binding.locationBtn.text = getString(R.string.btn_onLocation)
                binding.statusText.text = getString(R.string.status_on)
                /*lifecycleScope.launch {
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
                }*/
                // フォアグラウンドサービスを開始する （アプリを閉じても監視を継続できる）
                ContextCompat.startForegroundService(
                    this,
                    Intent(
                        this,
                        ForegroundService::class.java
                    )
                )
                // 距離判定実行開始
//                distanceChecker.startChecking()
            }else{
                //locationGetter.stopLocationonUpdate()
                // フォアグラウンドサービス停止
                stopService(
                    Intent(
                        this,
                        ForegroundService::class.java
                    )
                )
                binding.locationBtn.text = getString(R.string.btn_offLocation)
                binding.statusText.text = getString(R.string.status_off)
//                Log.e("test","停止した一時停止${distanceChecker.stopSuccsesCount}")
//                Log.e("test","接近した一時停止${distanceChecker.stopPointsCount}")
                // 距離判定停止
//                distanceChecker.stopChecking()
>>>>>>> Stashed changes
            }
        }
    }
}