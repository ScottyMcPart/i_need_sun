package com.example.i_need_sun.solar
import kotlin.math.*

data class SunPosition(
    val elevDeg: Double,
    val azDeg: Double
)

fun sunPosition(minuteOfDay: Int): SunPosition{
    val decl = Math.toRadians(23.4)
    val lat = Math.toRadians(51.0)
    val ha = Math.toRadians((minuteOfDay / 60.0 - 12) * 15)

    val sinE = sin(lat)*sin(decl) +
            cos(lat)*cos(decl)*cos(ha)
    val elev = asin(sinE.coerceIn(-1.0,1.0))

    val cosA = (sin(decl) - sin(lat)*sinE) /
            (cos(lat)*cos(elev))
    var az = Math.toDegrees(
        acos(cosA.coerceIn(-1.0,1.0)))
    if (ha > 0) az = 360 - az
    return SunPosition(Math.toDegrees(elev), az)

}
