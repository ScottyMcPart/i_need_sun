package com.example.i_need_sun.domain.model

import kotlin.math.*

fun geoToPixelOffset(observer: GeoPoint,
                     target: GeoPoint,
                     pixelsPerMeter: Int = PIXELS_PER_METER
): Pair<Int, Int> {
    val metersPerDegLat = 111_320.0
    val metersPerDegLng = 111_320.0 * cos(Math.toRadians(observer.lat))

    val dNorthM = (target.lat - observer.lat) * metersPerDegLat
    val dEastM  = (target.lng - observer.lng) * metersPerDegLng

    val dx = (dEastM   * pixelsPerMeter).toInt()
    val dy = (-dNorthM * pixelsPerMeter).toInt()
    return Pair(dx, dy)
}

fun buildingRectFromGeo(
    observer: GeoPoint,
    buildingGeo: GeoPoint,
    widthM: Double,
    depthM: Double,
    canvasCentreX: Int,
    canvasCentreY: Int,
    pixelsPerMeter: Int = PIXELS_PER_METER
): BuildingRect {
    val (dx, dy) = geoToPixelOffset(observer, buildingGeo, pixelsPerMeter)
    val wPx = (widthM * pixelsPerMeter).toInt()
    val hPx = (depthM * pixelsPerMeter).toInt()
    return BuildingRect(
        x   = canvasCentreX + dx - wPx / 2,
        y   = canvasCentreY + dy - hPx / 2,
        wPx = wPx,
        hPx = hPx
    )
}

// Same function reused for the observer area rectangle
fun observerRectFromGeo(
    canvasCentreX: Int,
    canvasCentreY: Int,
    pixelsPerMeter: Int = PIXELS_PER_METER
): BuildingRect {
    val wPx = (OBSERVER_WIDTH_M * pixelsPerMeter).toInt()
    val hPx = (OBSERVER_DEPTH_M * pixelsPerMeter).toInt()
    // Observer is the canvas centre, so the rect is centred there
    return BuildingRect(
        x   = canvasCentreX - wPx / 2,
        y   = canvasCentreY - hPx / 2,
        wPx = wPx,
        hPx = hPx
    )
}