package com.example.prototype

import android.Manifest
import android.content.Intent
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
import androidx.appcompat.app.AlertDialog
import android.widget.Button
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import android.os.Build
import android.widget.RadioGroup


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationGetter: LocationGetter
    private lateinit var nearStopSign: NearStopSign
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private var isMonitoring = false
    private lateinit var distanceChecker: DistanceChecker

    private lateinit var checker: FirstLaunchCheck
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checker = FirstLaunchCheck(this)
        //テスト中のみ(毎回初回起動判定にする)
        checker.resetFirstLaunch()
        if(checker.isFirstLaunch()) {
            val dialogView = layoutInflater.inflate(
                R.layout.dialog_mode_settings,
                null
            )
            val dialog =
                AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create()
            dialog.setCancelable(false)
            val radioMusic = dialogView.findViewById<RadioButton>(
                R.id.radioMusic
            )

            val radioVibe = dialogView.findViewById<RadioButton>(
                R.id.radioVibe
            )
            val radioBoth = dialogView.findViewById<RadioButton>(
                R.id.radioBoth
            )

            val saveMode = checker.getAlertMode()

            when (saveMode) {
                FirstLaunchCheck.MODE_SOUND -> {
                    radioMusic.isChecked = true
                    radioVibe.isChecked = false
                    radioBoth.isChecked = false
                }

                FirstLaunchCheck.MODE_VIBRATION -> {
                    radioMusic.isChecked = false
                    radioVibe.isChecked = true
                    radioBoth.isChecked = false
                }

                FirstLaunchCheck.MODE_BOTH -> {
                    radioMusic.isChecked = false
                    radioVibe.isChecked = false
                    radioBoth.isChecked = true
                }
            }

            radioMusic.setOnClickListener {
                radioMusic.isChecked = true
                radioVibe.isChecked = false
                radioBoth.isChecked = false
            }

            radioVibe.setOnClickListener {
                radioMusic.isChecked = false
                radioVibe.isChecked = true
                radioBoth.isChecked = false
            }

            radioBoth.setOnClickListener {
                radioMusic.isChecked = false
                radioVibe.isChecked = false
                radioBoth.isChecked = true
            }
            val btnOk = dialogView.findViewById<Button>(
                R.id.btnOk
            )
            btnOk.setOnClickListener {
                //ラジオボタンの選択したもの取得(現在はログになってます)
                if (radioMusic.isChecked) {
                    checker.saveAlertMode(
                        FirstLaunchCheck.MODE_SOUND
                    )

                    Log.e("MODE", "音声モード")

                } else if (radioVibe.isChecked) {
                    checker.saveAlertMode(
                        FirstLaunchCheck.MODE_VIBRATION
                    )

                    Log.e("MODE", "バイブモード")

                } else if (radioBoth.isChecked) {
                    checker.saveAlertMode(
                        FirstLaunchCheck.MODE_BOTH
                    )

                    Log.e("MODE", "音声+バイブモード")

                }
                dialog.dismiss()
                checker.setFirstLaunchFinished()
            }
            dialog.show()
        }
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
        binding.locationBtn.setOnClickListener() {
            isMonitoring = !isMonitoring
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
                //バックグラウンド追加分
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
                //バックグラウンド追加分 
                stopService(
                    Intent(
                        this,
                        ForegroundService::class.java
                    )
                )
                binding.locationBtn.text = getString(R.string.btn_offLocation)
//                Log.e("test","停止した一時停止${distanceChecker.stopSuccsesCount}")
//                Log.e("test","接近した一時停止${distanceChecker.stopPointsCount}")
                // 距離判定停止
//                distanceChecker.stopChecking()
            }

        }

        binding.btnReport.setOnClickListener {
            Log.e("btncheack","クリック")
            val intent = Intent(
                this,
                ReportActivity::class.java
            )
            intent.putExtra("STOP_SUCCSES_COUNT",distanceChecker.stopSuccsesCount)
            intent.putExtra("STOP_POINTS_COUNT",distanceChecker.stopPointsCount)
            startActivity(intent)
        }

        binding.btnSettings.setOnClickListener {
            val settingsView =layoutInflater.inflate(
                R.layout.activity_setting,
                null
            )
            val dialog = AlertDialog.Builder(this)
                .setView(settingsView)
                .create()

            val radioModeGroup = settingsView.findViewById<RadioGroup>(
                R.id. radioModeGroup
            )

            val Btnmap = settingsView.findViewById<Button>(R.id.btnReport)

            when (checker.getAlertMode()) {
                FirstLaunchCheck.MODE_SOUND -> {
                    radioModeGroup.check(R.id.radioSound)
                }

                FirstLaunchCheck.MODE_VIBRATION -> {
                    radioModeGroup.check(R.id.radioVibration)
                }

                FirstLaunchCheck.MODE_BOTH -> {
                    radioModeGroup.check(R.id.radioBoth)
                }

                else -> {
                    radioModeGroup.check(R.id.radioSound)
                }
            }

            radioModeGroup.setOnCheckedChangeListener { _: RadioGroup, checkedId: Int ->
                when (checkedId) {
                    R.id.radioSound -> {
                        checker.saveAlertMode(
                            FirstLaunchCheck.MODE_SOUND
                        )
                    }

                    R.id.radioVibration -> {
                        checker.saveAlertMode(
                            FirstLaunchCheck.MODE_VIBRATION
                        )
                    }

                    R.id.radioBoth -> {
                        checker.saveAlertMode(
                            FirstLaunchCheck.MODE_BOTH
                        )
                    }
                }
            }

            Btnmap.setOnClickListener {
                val intent = Intent (
                    this,
                    MapActivity::class.java
                )

                startActivity(intent)
                dialog.dismiss()
            }
            dialog.show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        locationGetter.stopLocationonUpdate()
        // 停止
//        distanceChecker.stopChecking()
    }
}

