package com.example.prototype
import android.content.Context
import android.location.Location
//import android.util.Log
//import androidx.annotation.UiContext
//import com.example.prototype.LocationGetter
//import com.google.gson.Gson
//import com.google.gson.JsonSyntaxException
import org.json.JSONArray
//import java.io.File
//import java.io.IOException

//data class

class NearStopSign() {
    private lateinit var location: Location
    val stopPoints = mutableListOf<Map<String, Any>>()
//    data class  StopPointJson(
//        val uniqueKey: String,
//        val bearing: Float,
//        val long: Double,
//        val lat: Double
//    )
//    data class StopPointsJson(
//        val list: List<StopPointJson>
//    )
    fun setStopPoints(context: Context){
        val inputjson = context.resources.openRawResource( R.raw.outputstop)
        val inputString = inputjson.bufferedReader().use { it.readText() }
        val inputArray = JSONArray(inputString)
        stopPoints.clear()
        for (i in 0 ..< inputArray.length()){
            val obj = inputArray.getJSONObject(i)
            stopPoints.add(
                mapOf(
                    "long" to obj.getDouble("lon"),
                    "lat" to obj.getDouble("lat"),
                    "bearing" to obj.getInt("az")
                )
            )
        }
    }
    private fun matchBearing(
        userBearing: Float,
        stopPoints: List<Map<String, Any>>
    ): List<Map<String, Any>> {
        return stopPoints.filter { stop ->
            val bearing = (stop["bearing"] as Int).toFloat()
            val diff = getBearingDiff(
                userBearing,bearing
            )
            diff <= 15f
        }

    }
    private fun getBearingDiff(a: Float,b: Float): Float{
        val diff = kotlin.math.abs(a-b)
        return kotlin.math.min(
            diff,
            360f-diff
        )
    }
    private fun matchNearestStop(nowLat: Double,nowLong: Double, matchBearingPoints:List<Map<String, Any>>):Map<String, Any>?{
        var results = FloatArray(3)
        val distances = mutableListOf<Float>()
        if (matchBearingPoints.isEmpty()) return null


        for (i in 0..< matchBearingPoints.size){
            Location.distanceBetween(nowLat,nowLong,matchBearingPoints[i]["lat"] as Double,matchBearingPoints[i]["long"] as Double, results)
            distances.add(results[0])
        }
        return matchBearingPoints[distances.indexOf(distances.minOrNull())]
    }
    //最寄りの一時停止返す関数
    fun matchStopSing(lat: Double,long: Double,bearing: Float,stopPoints: List<Map<String, Any>>):Map<String, Any>?{
        var matchBearingPoints = matchBearing(bearing,stopPoints)
        if (matchBearingPoints.isEmpty()){
            return null
        }
        return matchNearestStop(lat, long, matchBearingPoints)
    }
}
