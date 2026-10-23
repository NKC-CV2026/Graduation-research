package com.example.prototype

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.prototype.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.net.URL
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.osmdroid.tileprovider.tilesource.XYTileSource



class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var map: MapView

    private val markers = mutableListOf<Marker>()

    private lateinit var distanceChecker: DistanceChecker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        distanceChecker = DistanceChecker(this)
        //osmdroid設定
        Configuration.getInstance()
            .load(
                applicationContext,
                getSharedPreferences(
                    "osmdroid",
                    MODE_PRIVATE
                )
            )
        Configuration.getInstance().userAgentValue =
            "StopSignAlert-GraduationProject/1.0"


        val internalDataDir = applicationContext.filesDir
        Configuration.getInstance().osmdroidBasePath = internalDataDir

        Configuration.getInstance().osmdroidTileCache =
            java.io.File(internalDataDir, "osmdroid/tiles")

        setContentView(R.layout.activity_map)

//        map = findViewById(R.id.map)
//        map.setTileSource(TileSourceFactory.MAPNIK)

        map = findViewById(R.id.map)

        val tileSource = XYTileSource(
            "OSMFrance",
            0,
            19,
            256,
            ".png",
            arrayOf("https://a.tile.openstreetmap.fr/osmfr/")
        )

        map.setTileSource(tileSource)



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
        var point = GeoPoint(
            35.12574345899065,
            136.91242805166993
        )
        //テスト用データ挿入
//        distanceChecker.unStopSign.add("23202602018069700000000003048802")
//        distanceChecker.unStopSign.add("23202602018070000000000003049108")
        lifecycleScope.launch {
            //マーカーを追加する処理
            if (distanceChecker.unStopSign.isEmpty()) {
                addMarker(
                    35.12574345899065,
                    136.91242805166993,
                    "神宮前駅"
                )
            } else {
                try {
                    for (uniquekey in distanceChecker.unStopSign) {
                        val url =
                            URL("https://8etztd7m61.execute-api.ap-northeast-3.amazonaws.com/api/v1/stop-points/${uniquekey}")
                        val inputString = withContext(Dispatchers.IO) {
                            url.readText()
                        }
                        if (!inputString.isEmpty()) {

                            val obj = JSONObject(inputString)
                            addMarker(
                                obj.getDouble("lat"),
                                obj.getDouble("long"),
                                uniquekey
                            )
                            point = GeoPoint(
                                obj.getDouble("lat"),
                                obj.getDouble("long")
                            )
//                            val inputArray = JSONArray(inputString)
//                            // 前回のデータが残らないように一度空にする
//                            markers.clear()
//                            for (i in 0..<inputArray.length()) {
//                                val obj = inputArray.getJSONObject(i)
//                                addMarker(obj.getDouble("lat"), obj.getDouble("long"), uniquekey)
//                                if (i == inputArray.length() - 1) {
//                                    point = GeoPoint(
//                                        obj.getDouble("lat"), obj.getDouble("long")
//                                    )
//                                }
//                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("map", "通信エラー", e)
                }

            }
            //マップ再読み込みみたいな
            map.invalidate()


            map.controller.setCenter(point)

            val backButton = findViewById<Button>(R.id.btnBack)
            backButton.setOnClickListener {
                finish()
            }
        }
    }
    //マーカーを追加する関数
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