package com.example.i_need_sun.ui.view

import android.content.Context
import android.graphics.*
import android.view.View
import com.example.i_need_sun.domain.model.TEST_BUILDING
import com.example.i_need_sun.domain.solar.shadowPolygon
import com.example.i_need_sun.domain.solar.shadowVector
import com.example.i_need_sun.domain.solar.sunPosition
import android.util.AttributeSet
import android.util.TypedValue
import com.example.i_need_sun.R

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

    // ── Read colors from the active theme ────────────────────────────────
    private fun themeColor(attr: Int): Int {
        val tv = TypedValue()
        context.theme.resolveAttribute(attr, tv, true)
        return tv.data
    }

    // Paints are initialized lazily so they always pick up the current theme,
    // including if the user switches dark mode while the app is running
    private val groundPaint    get() = Paint().apply {
        color = themeColor(R.attr.colorGround)
        style = Paint.Style.FILL
    }
    private val shadowPaint    get() = Paint().apply {
        color = Color.BLACK
        alpha = 80
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val buildingFill   get() = Paint().apply {
        color = themeColor(R.attr.colorBuildingFill)
        style = Paint.Style.FILL
    }
    private val buildingStroke get() = Paint().apply {
        color = themeColor(R.attr.colorBuildingOutline)
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }
    private val compassPaint   get() = Paint().apply {
        color = themeColor(R.attr.colorCompass)
        strokeWidth = 2f
        isAntiAlias = true
    }
    private val compassText    get() = Paint().apply {
        color = themeColor(R.attr.colorCompass)
        textSize = 28f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    // ─────────────────────────────────────────────────────────────────────

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), groundPaint)

        // 2. Shadow
        val sun = sunPosition(minuteOfDay)
        val sv  = shadowVector(sun)
        if (sv != null) {
            val pts  = shadowPolygon(TEST_BUILDING, sv)
            val path = Path()
            path.moveTo(pts[0].x, pts[0].y)
            for (i in 1 until pts.size) path.lineTo(pts[i].x, pts[i].y)
            path.close()
            canvas.drawPath(path, shadowPaint)
        }

        // 3. Building
        val b = TEST_BUILDING
        val l = b.x.toFloat()
        val t = b.y.toFloat()
        val r = (b.x + b.wPx).toFloat()
        val btm = (b.y + b.hPx).toFloat()
        canvas.drawRect(l, t, r, btm, buildingFill)
        canvas.drawRect(l, t, r, btm, buildingStroke)

        // 4. North arrow
        canvas.drawText("N", 40f, 50f, compassText)
        canvas.drawLine(40f, 60f, 40f, 100f, compassPaint)
        canvas.drawLine(30f, 80f, 40f,  60f, compassPaint)
        canvas.drawLine(50f, 80f, 40f,  60f, compassPaint)
    }
}