package com.example.i_need_sun.domain.solar

import android.graphics.PointF
import com.example.i_need_sun.domain.model.BuildingRect
import com.example.i_need_sun.domain.model.PIXELS_PER_METER
import kotlin.math.*

data class ShadowVec(val dx: Float, val dy: Float)

// Returns null when the sun is below the horizon
fun shadowVector(sun: SunPosition, heightM: Double): ShadowVec? {
    if (!sun.isAboveHorizon) return null

    // Length on the ground = building height / tan(elevation)
    val lengthPx = (heightM /
            tan(Math.toRadians(sun.elevDeg))) * PIXELS_PER_METER

    // Shadow falls directly opposite the sun
    val shadowAzRad = Math.toRadians(sun.azDeg + 180.0)

    return ShadowVec(
        dx =  sin(shadowAzRad).toFloat() * lengthPx.toFloat(),
        dy = -cos(shadowAzRad).toFloat() * lengthPx.toFloat()
        //   ^ minus because canvas y grows downward, North = up
    )
}

// Project the 4 building corners by the shadow vector,
// then return the convex hull of all 8 points
fun shadowPolygon(b: BuildingRect, sv: ShadowVec): List<PointF> {
    val corners = listOf(
        PointF(b.x.toFloat(),           b.y.toFloat()),
        PointF((b.x + b.wPx).toFloat(), b.y.toFloat()),
        PointF((b.x + b.wPx).toFloat(), (b.y + b.hPx).toFloat()),
        PointF(b.x.toFloat(),           (b.y + b.hPx).toFloat())
    )
    val projected = corners.map { c ->
        PointF(c.x + sv.dx, c.y + sv.dy)
    }
    return convexHull(corners + projected)
}

// Gift-wrapping convex hull
private fun convexHull(pts: List<PointF>): List<PointF> {
    if (pts.size < 3) return pts
    var start = 0
    for (i in 1 until pts.size) {
        if (pts[i].x < pts[start].x ||
            (pts[i].x == pts[start].x && pts[i].y < pts[start].y))
            start = i
    }
    val hull = mutableListOf<PointF>()
    var current = start
    do {
        hull.add(pts[current])
        var next = (current + 1) % pts.size
        for (j in pts.indices) {
            val cross = (pts[next].x - pts[current].x) *
                    (pts[j].y  - pts[current].y) -
                    (pts[next].y - pts[current].y) *
                    (pts[j].x  - pts[current].x)
            if (cross < 0f) next = j
        }
        current = next
    } while (current != start && hull.size <= pts.size)
    return hull
}
fun isPointInShadow(
    px: Float,
    py: Float,
    building: BuildingRect,
    sv: ShadowVec,

): Boolean {
    val pts = shadowPolygon(building, sv)
    var inside = false
    var j = pts.size-1
    for(i in pts.indices){
        val xi = pts[i].x
        val yi = pts[j].y
        val xj = pts[i].x
        val yj = pts[j].y

        val slope = (xj - xi)/(yj - yi)
        val dx = slope * (py -yi)

        val intersects = (yi > py) != (yj > py) &&
                px < dx + xi
        if (intersects) inside = !inside
        j = i
    }
    return inside
}