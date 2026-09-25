package com.example.i_need_sun.ui

import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.i_need_sun.R
import com.example.i_need_sun.domain.model.SCENES
import com.example.i_need_sun.domain.solar.sunPosition
import com.example.i_need_sun.ui.adapter.SceneAdapter


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val seekBar     = findViewById<SeekBar>(R.id.seekBarTime)
        val tvTime      = findViewById<TextView>(R.id.tvTime)
        val tvElevation = findViewById<TextView>(R.id.tvElevation)
        val tvAzimuth   = findViewById<TextView>(R.id.tvAzimuth)
        val recycler    = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerScenes)

        // Set up the RecyclerView
        val adapter = SceneAdapter(SCENES)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter


        // ── Calculate real sunrise and sunset for today ───────────────────
        val (sunriseMin, sunsetMin) = calcSunriseSunset()
        // Add 30 min padding so the slider doesn't start/end exactly at horizon
        seekBar.min      = (sunriseMin - 30).coerceAtLeast(0)
        seekBar.max      = (sunsetMin  + 30).coerceAtMost(23 * 60 + 59)
        seekBar.progress = 720
        // ─────────────────────────────────────────────────────────────────

        fun updateLabels(minutes: Int) {
            val h = minutes / 60
            val m = minutes % 60
            tvTime.text = "%02d:%02d".format(h, m)

            val lat = SCENES.first().observer.geo.lat
            val sun = sunPosition(minutes, lat)
            tvElevation.text = if (sun.isAboveHorizon)
                "Elevation: %.1f°".format(sun.elevDeg)
            else
                "Elevation: below horizon"
            tvAzimuth.text = "Azimuth: %d°".format(sun.azDeg.toInt())
        }


        updateLabels(seekBar.progress)
        adapter.updateTime(seekBar.progress)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                adapter.updateTime(progress)
                updateLabels(progress)
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })

    }

    /**
     * Scans the full day in 1-minute steps and finds the first minute
     * the sun crosses above the horizon (sunrise) and the last minute
     * it is still above it (sunset).
     *
     * Returns Pair(sunriseMinute, sunsetMinute).
     * Falls back to 06:00 / 18:00 if the sun never rises (polar night)
     * or never sets (midnight sun).
     */
    private fun calcSunriseSunset(): Pair<Int, Int> {
        var sunriseMin = 360  // fallback 06:00
        var sunsetMin  = 1080 // fallback 18:00
        var foundSunrise = false

        val lat = SCENES.first().observer.geo.lat

        for (min in 0 until 24 * 60) {
            val above = sunPosition(min, lat).isAboveHorizon
            if (!foundSunrise && above) {
                sunriseMin = min
                foundSunrise = true
            }
            if (foundSunrise && above) {
                sunsetMin = min  // keep updating — last above-horizon minute wins
            }
        }
        return Pair(sunriseMin, sunsetMin)
    }
}