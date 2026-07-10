package com.example.prototype

import android.content.Context
import android.location.Location
import org.json.JSONArray
import android.util.Log
import java.io.File
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL


// 一時停止標識のデータを読み込み
// 現在地・進行方向に合う一時停止標識を探すクラス
class NearStopSign() {
    // JSONから読み込んだ一時停止標識の一覧を保存するリスト
    val stopPoints = mutableListOf<Map<String, Any>>()

    // rawフォルダ内のJSONファイルから一時停止標識データを読み込む
    fun setStopPoints(context: Context,lat: Double,long: Double){
        Log.e("test","data:${downloadJson(lat,long)}")
//        val inputjson = URL("https://8etztd7m61.execute-api.ap-northeast-3.amazonaws.com/api/v1/stop-points?lat=$lat&long=$long&radius=300")//context.resources.openRawResource( R.raw.outputstop)
//        val file = File(context.filesDir, "stop_points.json")
        val url = URL("https://8etztd7m61.execute-api.ap-northeast-3.amazonaws.com/api/v1/stop-points?lat=$lat&long=$long&radius=300")
        val inputString = url.readText()
//            if (file.exists()){
//            file.readText()
////            downloadJson(lat,long)
//        }else {
//            inputjson.bufferedReader().use { it.readText() }
//        }
        val inputArray = JSONArray(inputString)
        // 前回のデータが残らないように一度空にする
        stopPoints.clear()
        for (i in 0 ..< inputArray.length()){
            val obj = inputArray.getJSONObject(i)
            stopPoints.add(
                mapOf(
                    "uniqueKey" to obj.getString("uniqueKey"), // 標識を識別するID
                    "long" to obj.getDouble("lon"),  // 経度
                    "lat" to obj.getDouble("lat"), // 緯度
                    "bearing" to obj.getInt("az") // 標識の進入方向
                )
            )
        }
    }

    // 自転車の進行方向と標識の向きが近いものだけを残す
    private fun matchBearing(
        userBearing: Float,
        stopPoints: List<Map<String, Any>>
    ): List<Map<String, Any>> {
        return stopPoints.filter { stop ->
            val bearing = (stop["bearing"] as Int).toFloat()
            val diff = getBearingDiff(
                userBearing,bearing
            )
            // 進行方向との差が15度以内の標識だけ候補にする
            diff <= 15f
        }

    }

    // 2つの方位角の差を0〜180度の範囲で求める
    private fun getBearingDiff(a: Float,b: Float): Float{
        val diff = kotlin.math.abs(a-b)
        // 例：350度と10度は差340度ではなく20度として扱う
        return kotlin.math.min(
            diff,
            360f-diff
        )
    }
    // 候補の中から現在地に一番近い標識を返す
    private fun matchNearestStop(nowLat: Double,nowLong: Double, matchBearingPoints:List<Map<String, Any>>):Map<String, Any>?{
        var results = FloatArray(3)
        val distances = mutableListOf<Float>()
        if (matchBearingPoints.isEmpty()) return null
        // 各候補との距離を計算する
        for (i in 0..< matchBearingPoints.size){
            Location.distanceBetween(nowLat,nowLong,matchBearingPoints[i]["lat"] as Double,matchBearingPoints[i]["long"] as Double, results)
            distances.add(results[0])
        }
        // 距離が一番短い標識を返す
        return matchBearingPoints[distances.indexOf(distances.minOrNull())]
    }
    // 現在地・進行方向から、反応させるべき一時停止標識を1つ返す
    fun matchStopSing(lat: Double,long: Double,bearing: Float,stopPoints: List<Map<String, Any>>):Map<String, Any>?{
        // まず進行方向が合う標識だけに絞る
        var matchBearingPoints =
            matchBearing(bearing, stopPoints)

        if (matchBearingPoints.isEmpty()) {
            return null
        }
        // 次に、現在走っている道路上に近い標識だけに絞る
        val matchRoadPoints =
            matchRoadSide(
                lat,
                long,
                bearing,
                matchBearingPoints
            )

        if (matchRoadPoints.isEmpty()) {
            return null
        }
        // 最後に、その中で一番近い標識を返す
        return matchNearestStop(
            lat,
            long,
            matchRoadPoints
        )
    }
    fun downloadJson(lat: Double,long: Double) : String{
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://8etztd7m61.execute-api.ap-northeast-3.amazonaws.com/api/v1/stop-points?lat=$lat&long=$long&radius=300")
            .build()

        val response = client.newCall(request).execute()
        return response.toString()
//        println(response.body?.string())
    }
    // 横の道や後ろ側の標識を除外するための絞り込み
    private fun matchRoadSide(
        nowLat: Double,
        nowLong: Double,
        userBearing: Float,
        points: List<Map<String, Any>>
    ): List<Map<String, Any>> {

        return points.filter { stop ->

            val results = FloatArray(3)
            // 現在地から標識までの距離と方位角を求める
            Location.distanceBetween(
                nowLat,
                nowLong,
                stop["lat"] as Double,
                stop["long"] as Double,
                results
            )

            val distance = results[0]    // 現在地から標識までの距離
            val bearingToStop = results[1]  // 現在地から標識への方位角
//            自分の進行方向と、標識方向の角度差
//            diff = 0°    → 真正面
//            diff = 90°   → 真横
//            diff = 180°  → 真後ろ
            val diff = getBearingDiff(
                userBearing,
                bearingToStop
            )
            // 進行方向の直線から、標識が横にどれだけずれているかを計算する
            val roadCheck =
                kotlin.math.abs(
                    distance * kotlin.math.sin(
                        Math.toRadians(diff.toDouble())
                    )
                )

            // key：標識の識別番号
            // distance：現在地から標識までの直線距離
            // bearingToStop：現在地から標識へ向かう方位角
            // diff：自分の進行方向と標識方向の角度差
            // roadCheck：進行方向の直線から標識が横に何mずれているか
//            Log.e(
//                "ROAD_CHECK",
//                "key=${stop["uniqueKey"]} distance=$distance bearingToStop=$bearingToStop diff=$diff roadCheck=$roadCheck"
//            )

            // diff <= 120f：進行方向との差が120度以内の標識だけ残す
            //0〜120度 → 残す 120〜180度 → 除外 (厳密に「後ろを全部除外」したいなら diff <= 90f)
            // roadCheck <= 20f：進行方向の直線から横ズレ20m以内の標識だけ残す
            return@filter diff <= 120f && roadCheck <= 20f
        }
    }
}
