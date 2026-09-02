package com.example.i_need_sun.ui

import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.i_need_sun.R
import com.example.i_need_sun.domain.solar.sunPosition
import com.example.i_need_sun.ui.view.ShadowView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val shadowView  = findViewById<ShadowView>(R.id.shadowView)
        val seekBar     = findViewById<SeekBar>(R.id.seekBarTime)
        val tvTime      = findViewById<TextView>(R.id.tvTime)
        val tvElevation = findViewById<TextView>(R.id.tvElevation)
        val tvAzimuth   = findViewById<TextView>(R.id.tvAzimuth)

        // ── Calculate real sunrise and sunset for today ───────────────────
        val (sunriseMin, sunsetMin) = calcSunriseSunset()
        // Add 30 min padding so the slider doesn't start/end exactly at horizon
        seekBar.min      = (sunriseMin - 30).coerceAtLeast(0)
        seekBar.max      = (sunsetMin  + 30).coerceAtMost(23 * 60 + 59)
        seekBar.progress = 720 // start at noon
        // ─────────────────────────────────────────────────────────────────

        fun updateLabels(minutes: Int) {
            val h = minutes / 60
            val m = minutes % 60
            tvTime.text = "%02d:%02d".format(h, m)

            val sun = sunPosition(minutes)
            tvElevation.text = if (sun.isAboveHorizon)
                "Elevation: %.1f°".format(sun.elevDeg)
            else
                "Elevation: below horizon"
            tvAzimuth.text = "Azimuth: %d°".format(sun.azDeg.toInt())
        }


        updateLabels(seekBar.progress)
        shadowView.minuteOfDay = seekBar.progress

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                shadowView.minuteOfDay = progress
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

        for (min in 0 until 24 * 60) {
            val above = sunPosition(min).isAboveHorizon
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