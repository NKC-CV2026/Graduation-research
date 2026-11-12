// 非同期
package com.example.prototype

import android.content.Context
import android.location.Location
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sin

// 一時停止標識のデータ構造を定義（型安全にするためMapからdata classに変更）
data class StopPoint(
    val uniqueKey: String,
    val long: Double,
    val lat: Double,
    val bearing: Float
)

class NearStopSign {
    // 取得した一時停止標識の一覧
    val stopPoints = mutableListOf<StopPoint>()
    private val client = OkHttpClient()

    /**
     * APIから一時停止標識データを非同期で取得し、リストを更新する
     * ※メインスレッドから安全に呼び出せるよう suspend 関数にしています
     */
    suspend fun setStopPoints(lat: Double, long: Double): Boolean = withContext(Dispatchers.IO) {
        Log.d("url","https://8etztd7m61.execute-api.ap-northeast-3.amazonaws.com/api/v1/stop-points?lat=$lat&long=$long&radius=300")
        val url = "https://8etztd7m61.execute-api.ap-northeast-3.amazonaws.com/api/v1/stop-points?lat=$lat&long=$long&radius=300"
        val request = Request.Builder().url(url).build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext false

                val inputString = response.body?.string() ?: return@withContext false
                val inputArray = JSONArray(inputString)

                // バックグラウンドでパース処理
                val newPoints = mutableListOf<StopPoint>()
                for (i in 0 until inputArray.length()) {
                    val obj = inputArray.getJSONObject(i)
                    val uniqueKey = obj.optString("uniqueKey", "")
                    val stopLng = obj.optDouble("long", -1.0)
                    val stopLat = obj.optDouble("lat", -1.0)
                    val az = obj.optInt("az", -1)

                    if (uniqueKey.isNotEmpty() && stopLng != -1.0 && stopLat != -1.0 && az != -1) {
                        newPoints.add(
                            StopPoint(
                                uniqueKey = uniqueKey,
                                long = stopLng,
                                lat = stopLat,
                                bearing = az.toFloat()
                            )
                        )
                    }
                }

                // メインスレッドでリストを更新
                withContext(Dispatchers.Main) {
                    stopPoints.clear()
                    stopPoints.addAll(newPoints)
                }
                return@withContext true
            }
        } catch (e: Exception) {
            Log.e("NearStopSign", "通信またはパースエラー", e)
            return@withContext false
        }
    }

    /**
     * 現在地・進行方向から、反応させるべき一時停止標識を1つ返す
     */
    fun matchStopSign(lat: Double, lng: Double, userBearing: Float): StopPoint? {
        if (stopPoints.isEmpty()) return null

        // 1. 進行方向が合う標識だけに絞る
        val matchBearingPoints = matchBearing(userBearing, stopPoints)
        if (matchBearingPoints.isEmpty()) return null

        // 2. 現在走っている道路上に近い標識だけに絞る
        val matchRoadPoints = matchRoadSide(lat, lng, userBearing, matchBearingPoints)
        if (matchRoadPoints.isEmpty()) return null

        // 3. その中で一番近い標識を返す
        return matchNearestStop(lat, lng, matchRoadPoints)
    }

    // 自転車の進行方向と標識の向きが近いものだけを残す（15度以内）
    private fun matchBearing(userBearing: Float, points: List<StopPoint>): List<StopPoint> {
        return points.filter { stop ->
            val diff = getBearingDiff(userBearing, stop.bearing)
            diff <= 15f
        }
    }

    // 横の道や後ろ側の標識を除外するための絞り込み
    private fun matchRoadSide(
        nowLat: Double,
        nowLng: Double,
        userBearing: Float,
        points: List<StopPoint>
    ): List<StopPoint> {
        return points.filter { stop ->
            val results = FloatArray(3)
            Location.distanceBetween(nowLat, nowLng, stop.lat, stop.long, results)

            val distance = results[0]

            // Location.distanceBetween が返す方位角 (-180〜180) を 0〜360 に正規化
            val bearingToStop = if (results[1] < 0) results[1] + 360f else results[1]

            // 自分の進行方向と、標識方向の角度差
            val diff = getBearingDiff(userBearing, bearingToStop)

            // 進行方向の直線から、標識が横にどれだけずれているかを計算（メートル）
            val roadCheck = abs(distance * sin(Math.toRadians(diff.toDouble())))

            // 前方（90度以内）かつ 横ズレが20m以内の標識だけ残す
            diff <= 90f && roadCheck <= 20f
        }
    }

    // 候補の中から現在地に一番近い標識を返す
    private fun matchNearestStop(nowLat: Double, nowLng: Double, points: List<StopPoint>): StopPoint? {
        if (points.isEmpty()) return null

        val results = FloatArray(3)
        return points.minByOrNull { stop ->
            Location.distanceBetween(nowLat, nowLng, stop.lat, stop.long, results)
            results[0] // 距離が一番短いものを取得
        }
    }

    // 2つの方位角の差を0〜180度の範囲で求める
    private fun getBearingDiff(a: Float, b: Float): Float {
        val diff = abs(a - b) % 360f
        return min(diff, 360f - diff)
    }
}