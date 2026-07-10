package com.example.prototype

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.prototype.databinding.ActivityReportBinding

// 一時停止結果を表示する画面
class ReportActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityReportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // レイアウト読み込み
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // ForegroundServiceで保存したレポートデータを取得
        // SharedPreferencesを利用して、 サービス終了時に保存した結果を読み込む
        val pref = getSharedPreferences(
            "report"
            ,MODE_PRIVATE
        )
        // 一時停止成功回数
        val stopSuccsesCount = pref.getInt("STOP_SUCCSES_COUNT",0)
        // 通過した一時停止標識の総数
        val stopPointsCount = pref.getInt("STOP_POINTS_COUNT",0)
        // レポート画面へ結果を表示
        binding.txtReport.text = "一時停止した回数 : ${stopSuccsesCount}回\n" + "一時停止総数 : ${stopPointsCount}回"
        // レポート画面を閉じる
        binding.btnClose.setOnClickListener {
            finish()
        }
    }
}