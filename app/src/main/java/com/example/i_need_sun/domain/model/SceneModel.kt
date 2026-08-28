package com.example.i_need_sun.domain.model

const val PIXELS_PER_METER = 2
const val BUILDING_HEIGHT_M = 20.0

data class BuildingRect(
    val x: Int,
    val y: Int,
    val wPx: Int,
    val hPx: Int
)

val TEST_BUILDING = BuildingRect(
    x = 200, y = 300, wPx = 100, hPx = 80
) // 50 m × 40 m footprint