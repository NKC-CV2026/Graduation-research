package com.example.prototype


import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan






fun vincentyDistance(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    // WGS84楕円体
    val a = 6378137.0              // 長半径 (m)
    val f = 1 / 298.257223563      // 扁平率
    val b = (1 - f) * a            // 短半径

    val phi1 = Math.toRadians(lat1)
    val phi2 = Math.toRadians(lat2)
    val L = Math.toRadians(lon2 - lon1)

    val U1 = atan((1 - f) * tan(phi1))
    val U2 = atan((1 - f) * tan(phi2))

    val sinU1 = sin(U1)
    val cosU1 = cos(U1)
    val sinU2 = sin(U2)
    val cosU2 = cos(U2)

    var lambda = L
    var lambdaPrev: Double
    val maxIter = 1000
    var iter = 0

    var sinSigma: Double
    var cosSigma: Double
    var sigma: Double
    var sinAlpha: Double
    var cosSqAlpha: Double
    var cos2SigmaM: Double

    do {
        val sinLambda = sin(lambda)
        val cosLambda = cos(lambda)

        sinSigma = sqrt(
            (cosU2 * sinLambda).pow(2.0) +
                    (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda).pow(2.0)
        )

        if (sinSigma == 0.0) return 0.0 // 同一点

        cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda
        sigma = atan2(sinSigma, cosSigma)

        sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma
        cosSqAlpha = 1 - sinAlpha.pow(2.0)

        cos2SigmaM = if (cosSqAlpha != 0.0)
            cosSigma - (2 * sinU1 * sinU2 / cosSqAlpha)
        else
            0.0 // 赤道上

        val C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha))

        lambdaPrev = lambda
        lambda = L + (1 - C) * f * sinAlpha *
                (sigma + C * sinSigma *
                        (cos2SigmaM + C * cosSigma *
                                (-1 + 2 * cos2SigmaM.pow(2.0))))

    } while (abs(lambda - lambdaPrev) > 1e-12 && ++iter < maxIter)

    val uSq = cosSqAlpha * (a * a - b * b) / (b * b)
    val A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)))
    val B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)))

    val deltaSigma = B * sinSigma *
            (cos2SigmaM + B / 4 *
                    (cosSigma * (-1 + 2 * cos2SigmaM.pow(2.0)) -
                            B / 6 * cos2SigmaM *
                            (-3 + 4 * sinSigma.pow(2.0)) *
                            (-3 + 4 * cos2SigmaM.pow(2.0))))

    val s = b * A * (sigma - deltaSigma)

    return s // メートル
}

fun mainDistance(
    fl: Double, fi: Double,
    sl: Double, si: Double
): Double {
    val tokyo = Pair(fl, fi)
    val osaka = Pair(sl, si)

    val distance = vincentyDistance(
        tokyo.first, tokyo.second,
        osaka.first, osaka.second
    )

    println("距離: %.3f m".format(distance))
    return distance
}
val distance: Double = mainDistance(35.4122, 139.413, 34.4138, 135.3008)
