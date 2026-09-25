package com.example.i_need_sun.domain.model

const val PIXELS_PER_METER = 2

data class GeoPoint(
    val lat: Double,
    val lng: Double
)

data class Building(
    val geo: GeoPoint,
    val widthM: Double,
    val depthM: Double,
    val heightM: Double,
    val label: String = ""
)

data class BuildingRect(
    val x: Int,
    val y: Int,
    val wPx: Int,
    val hPx: Int
)

data class ObserverArea(
    val geo: GeoPoint,
    val widthM: Double  = 10.0,
    val depthM: Double  =  6.0
)

data class SceneConfig(
    val name: String,
    val observer: ObserverArea,
    val buildings: List<Building>
)

// ── Sample scenes ────────────────────────────────────────────────────────────

val SCENES = listOf(

    SceneConfig(
        name = "My Balcony",
        observer = ObserverArea(
            geo = GeoPoint(lat = 51.50720, lng = 6.99934)
        ),
        buildings = listOf(
            Building(
                geo     = GeoPoint(lat = 51.50780, lng = 6.99934),
                widthM  = 20.0, depthM = 15.0, heightM = 20.0,
                label   = "North block"
            ),
            Building(
                geo     = GeoPoint(lat = 51.50730, lng = 6.99980),
                widthM  = 12.0, depthM = 10.0, heightM = 30.0,
                label   = "East tower"
            )
        )
    ),

    SceneConfig(
        name = "Garden",
        observer = ObserverArea(
            geo = GeoPoint(lat = 51.50600, lng = 6.99800)
        ),
        buildings = listOf(
            Building(
                geo     = GeoPoint(lat = 51.50650, lng = 6.99800),
                widthM  = 30.0, depthM = 12.0, heightM = 10.0,
                label   = "Low shed"
            )
        )
    ),

    SceneConfig(
        name = "Park bench",
        observer = ObserverArea(
            geo = GeoPoint(lat = 51.50500, lng = 6.99700)
        ),
        buildings = listOf(
            Building(
                geo     = GeoPoint(lat = 51.50560, lng = 6.99680),
                widthM  = 40.0, depthM = 20.0, heightM = 35.0,
                label   = "Office block"
            ),
            Building(
                geo     = GeoPoint(lat = 51.50510, lng = 6.99750),
                widthM  = 15.0, depthM = 15.0, heightM = 15.0,
                label   = "Small annex"
            ),
            Building(
                geo     = GeoPoint(lat = 51.50480, lng = 6.99710),
                widthM  = 10.0, depthM =  8.0, heightM =  8.0,
                label   = "Kiosk"
            )
        )
    )
)