package com.example.i_need_sun.domain.solar

import android.icu.util.Calendar
import kotlin.math.*

data class SunPosition(
    val elevDeg: Double,
    val azDeg: Double
) {
    val isAboveHorizon: Boolean get() = elevDeg > 0.5
}

fun sunPosition(minuteOfDay: Int): SunPosition {

    //---------Calaculate declination from today's date----------------
    val cal = Calendar.getInstance()
    val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_YEAR)

    //standard declination Peaks at +23.45° around June 21, bottoms at -23.45° around Dec 21
    val declDeg = 23.45 * sin(
        Math.toRadians((360.0 / maxDays * (dayOfYear - 81)))
    )
    val decl = Math.toRadians(declDeg)
    //-----------------------------------------------------------------

    val lat  = Math.toRadians(51.0) // central Europe

    // Hour angle: 0 at solar noon, ±15° per hour
    val ha = Math.toRadians((minuteOfDay / 60.0 - 12.0) * 15.0)

    // Elevation
    val sinE = sin(lat) * sin(decl) + cos(lat) * cos(decl) * cos(ha)
    val elevRad = asin(sinE.coerceIn(-1.0, 1.0))

    // Azimuth (clockwise from North)
    val cosA = (sin(decl) - sin(lat) * sin(elevRad)) /
            (cos(lat) * cos(elevRad))
    var azDeg = Math.toDegrees(acos(cosA.coerceIn(-1.0, 1.0)))
    if (ha > 0) azDeg = 360.0 - azDeg // afternoon → west side

    return SunPosition(
        elevDeg = Math.toDegrees(elevRad),
        azDeg   = azDeg
    )
}