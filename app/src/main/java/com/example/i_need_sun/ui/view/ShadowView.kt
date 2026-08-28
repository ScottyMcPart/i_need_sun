package com.example.i_need_sun.ui.view

import android.content.Context
import android.graphics.*
import android.view.View
import com.example.i_need_sun.domain.model.TEST_BUILDING
import com.example.i_need_sun.domain.solar.shadowPolygon
import com.example.i_need_sun.domain.solar.shadowVector
import com.example.i_need_sun.domain.solar.sunPosition
import android.util.AttributeSet

class ShadowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var minuteOfDay: Int = 720
        set(value) {
            field = value
            invalidate() // triggers a redraw
        }

    private val shadowPaint = Paint().apply {
        color = Color.BLACK
        alpha = 70          // 0–255; 70 ≈ 27% opacity
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val buildingPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
    }
    private val outlinePaint = Paint().apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val northPaint = Paint().apply {
        color = Color.DKGRAY
        strokeWidth = 2f
        isAntiAlias = true
    }
    private val textPaint = Paint().apply {
        color = Color.DKGRAY
        textSize = 28f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. White background (ready for a map tile later)
        canvas.drawColor(Color.WHITE)

        // 2. Solar position for current time
        val sun = sunPosition(minuteOfDay)

        // 3. Shadow polygon — skip when sun is below horizon
        val sv = shadowVector(sun)
        if (sv != null) {
            val pts = shadowPolygon(TEST_BUILDING, sv)
            val path = Path()
            path.moveTo(pts[0].x, pts[0].y)
            for (i in 1 until pts.size) path.lineTo(pts[i].x, pts[i].y)
            path.close()
            canvas.drawPath(path, shadowPaint)
        }

        // 4. Building on top of shadow (painter's order)
        val b = TEST_BUILDING
        canvas.drawRect(
            b.x.toFloat(), b.y.toFloat(),
            (b.x + b.wPx).toFloat(), (b.y + b.hPx).toFloat(),
            buildingPaint
        )
        canvas.drawRect(
            b.x.toFloat(), b.y.toFloat(),
            (b.x + b.wPx).toFloat(), (b.y + b.hPx).toFloat(),
            outlinePaint
        )

        // 5. North arrow (top-left)
        canvas.drawText("N", 40f, 50f, textPaint)
        canvas.drawLine(40f, 60f, 40f, 100f, northPaint)
        canvas.drawLine(30f, 80f, 40f, 60f, northPaint)
        canvas.drawLine(50f, 80f, 40f, 60f, northPaint)
    }
}