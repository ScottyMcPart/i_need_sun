package com.example.i_need_sun.domain.model

const val PIXELS_PER_METER = 2
const val BUILDING_HEIGHT_M = 20.0

data class GeoPoint(
    val lat: Double,
    val lng: Double
)

data class BuildingRect(
    val x: Int,
    val y: Int,
    val wPx: Int,
    val hPx: Int
)
val BUILDING_GEO = GeoPoint(lat = 51.50800, lng = 6.99980)
const val BUILDING_WIDTH_M = 20.0
const val BUILDING_DEPTH_M = 15.0

//Obersver Area
const val OBSERVER_DISTANCE_M = BUILDING_DEPTH_M
const val OBSERVER_WIDTH_M = 10.0
const val OBSERVER_DEPTH_M = 6.0

// so the area sits flush against the building's south face
const val METRES_PER_DEG_LAT = 111_320.0
val OBSERVER = GeoPoint(
    lat = BUILDING_GEO.lat -
            (BUILDING_DEPTH_M / 2 + OBSERVER_DISTANCE_M) / METRES_PER_DEG_LAT,
    lng = BUILDING_GEO.lng
)
val TEST_BUILDING = BuildingRect(
    x = 200, y = 300, wPx = 100, hPx = 80
) // 50 m × 40 m footprint