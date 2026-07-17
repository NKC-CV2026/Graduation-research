package com.example.prototype

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.prototype.databinding.ActivityMainBinding
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var map: MapView

    private val markers = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        //osmdroid設定
        Configuration.getInstance()
            .load(
                applicationContext,
                getSharedPreferences(
                    "osmdroid",
                    MODE_PRIVATE
                )
            )

        setContentView(R.layout.activity_map)

        map = findViewById(R.id.map)

        val zoomInButton = findViewById<Button>(R.id.zoomInButton)
        val zoomOutButton = findViewById<Button>(R.id.zoomOutButton)

        //ズーム
        map.controller.setZoom(15.0)

        zoomInButton.setOnClickListener {
            map.controller.zoomIn()
        }
        zoomOutButton.setOnClickListener {
            map.controller.zoomOut()
        }


        //初期位置 (神宮前駅)今後、現在地周辺にしたり設定できるようにしたい
        val point = GeoPoint(
            35.12574345899065,
            136.91242805166993
        )

        //マーカーを追加する関数
        addMarker(
            35.12574345899065,
            136.91242805166993,
            "神宮前駅"
        )

        //マップ再読み込みみたいな
        map.invalidate()


        map.controller.setCenter(point)

        val backButton = findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun addMarker(lat: Double, lon: Double, title: String) {
        val marker = Marker(map)

        //マーカーの位置を指定
        marker.position = GeoPoint(
            lat,
            lon
        )
        //マーカーに付ける名前
        marker.title = title
        
        markers.add(marker)

        map.overlays.add(marker)

        map.invalidate()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}