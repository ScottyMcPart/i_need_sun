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

        // Set initial state
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
}