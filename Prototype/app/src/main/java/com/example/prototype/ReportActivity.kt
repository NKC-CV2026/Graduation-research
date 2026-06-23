package com.example.prototype

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prototype.databinding.ActivityReportBinding

class ReportActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityReportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val pref = getSharedPreferences(
            "report"
            ,MODE_PRIVATE
        )
        val stopSuccsesCount = pref.getInt("STOP_SUCCSES_COUNT",0)
        val stopPointsCount = pref.getInt("STOP_POINTS_COUNT",0)
        binding.txtReport.text = "一時停止した回数 : ${stopSuccsesCount}回\n" + "一時停止総数 : ${stopPointsCount}回"
        binding.btnClose.setOnClickListener {
            finish()
        }
    }
}