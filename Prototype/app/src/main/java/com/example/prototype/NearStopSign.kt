package com.example.prototype
import android.location.Location
import com.example.prototype.LocationGetter
class NearStopSign() {
    private lateinit var location: Location
    val stopPoints = mutableListOf<Map<String, Any>>()
    fun setStopPoints(){
        stopPoints.add(
            mapOf(
                "long" to 136.91254,
                "lat" to 35.12093,
                "bearing" to 190
            )
        )
        stopPoints.add(
            mapOf(
                "long" to 136.91677,
                "lat" to 35.1259,
                "bearing" to 90
            )
        )
        stopPoints.add(
            mapOf(
                "long" to 136.91575,
                "lat" to 35.12592,
                "bearing" to 90
            )
        )
    }
    fun matchBearing(
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
    fun getBearingDiff(a: Float,b: Float): Float{
        val diff = kotlin.math.abs(a-b)
        return kotlin.math.min(
            diff,
            360f-diff
        )
    }
    fun matchNearestStop(nowLat: Double,nowLong: Double, matchBearingPoints:List<Map<String, Any>>):Map<String, Any>{
        var results = FloatArray(3)
        val distances = mutableListOf<Float>()
        for (i in 0..< matchBearingPoints.size){
            Location.distanceBetween(nowLat,nowLong,matchBearingPoints[i]["lat"] as Double,matchBearingPoints[i]["long"] as Double, results)
            distances.add(results[0])
        }
        return matchBearingPoints[distances.indexOf(distances.minOrNull())]
    }
    fun matchStopSing(lat: Double,long: Double,bearing: Float,stopPoints: List<Map<String, Any>>):Map<String, Any>{
        return matchNearestStop(lat,long,matchBearing(bearing,stopPoints))
    }
}
