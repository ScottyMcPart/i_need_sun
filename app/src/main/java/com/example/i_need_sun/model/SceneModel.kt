package com.example.i_need_sun.model

const val PIXELS_PER_METER = 2

data class BuildingRect(
    val x: Int, val y: Int,
    val wPx: Int, val hPx: Int
)

val TEST_BUILDING = BuildingRect(
    x=90, y=100, wPx=100, hPx=80
)
