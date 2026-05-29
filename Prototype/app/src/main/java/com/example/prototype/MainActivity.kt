package com.example.prototype

import android.Manifest
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute
import android.os.Bundle
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prototype.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.example.prototype.LocationGetter
import com.example.prototype.NearStopSign


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationGetter: LocationGetter
    private lateinit var nearStopSign: NearStopSign
    private lateinit var location: Location
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
        nearStopSign = NearStopSign()
        binding.locationBtn.setOnClickListener() {

            isMonitoring = !isMonitoring
            if (isMonitoring){
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
                binding.locationBtn.text = "OFF"


            }else{
                locationGetter.stopLocationonUpdate()
                binding.locationBtn.text = "ON"
            }

        }
        // DistanceChecker生成
        distanceChecker = DistanceChecker(this)

        // 実行開始
        distanceChecker.startChecking()
    }
    override fun onDestroy() {
        super.onDestroy()

        // 停止
        distanceChecker.stopChecking()
    }
}

