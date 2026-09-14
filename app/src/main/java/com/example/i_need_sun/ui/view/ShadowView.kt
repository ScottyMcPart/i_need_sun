package com.example.i_need_sun.ui.view

import android.content.Context
import android.graphics.*
import android.view.View
import com.example.i_need_sun.domain.model.*
import com.example.i_need_sun.domain.solar.*
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
        val centreX = width / 2
        val centreY = height / 2

        // Build rects from geo coordinates
        val building = buildingRectFromGeo(
            observer      = OBSERVER,
            buildingGeo   = BUILDING_GEO,
            widthM        = BUILDING_WIDTH_M,
            depthM        = BUILDING_DEPTH_M,
            canvasCentreX = centreX,
            canvasCentreY = centreY
        )
        val observerRect = observerRectFromGeo(centreX, centreY)


        // 1. Background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), groundPaint)

        // 2. Shadow
        val sun = sunPosition(minuteOfDay)
        val sv  = shadowVector(sun)

        // 3. Check if observer area centre is inside the shadow polygon
        val inShadow = sv != null && isPointInShadow(
            px = centreX.toFloat(),
            py = centreY.toFloat(),
            building = building,
            sv = sv
        )

        // 4. Draw shadow polygon
        if (sv != null) {
            val pts  = shadowPolygon(building, sv)
            val path = Path()
            path.moveTo(pts[0].x, pts[0].y)
            for (i in 1 until pts.size) path.lineTo(pts[i].x, pts[i].y)
            path.close()
            canvas.drawPath(path, shadowPaint)
        }
        // 5. Observer area — green = sun, blue-grey = shadow
        val observerFillPaint = Paint().apply {
            color = if (inShadow)
                Color.parseColor("#5B7FA6")   // shadow: blue-grey
            else
                Color.parseColor("#6AAF6A")   // sun: green
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val observerStrokePaint = Paint().apply {
            color = if (inShadow)
                Color.parseColor("#3A5F86")
            else
                Color.parseColor("#3A8F3A")
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        val ol = observerRect.x.toFloat()
        val ot = observerRect.y.toFloat()
        val or_ = (observerRect.x + observerRect.wPx).toFloat()
        val ob  = (observerRect.y + observerRect.hPx).toFloat()
        canvas.drawRect(ol, ot, or_, ob, observerFillPaint)
        canvas.drawRect(ol, ot, or_, ob, observerStrokePaint)

        // 6. Building on top
        val l   = building.x.toFloat()
        val t   = building.y.toFloat()
        val r   = (building.x + building.wPx).toFloat()
        val btm = (building.y + building.hPx).toFloat()
        canvas.drawRect(l, t, r, btm, buildingFill)
        canvas.drawRect(l, t, r, btm, buildingStroke)

        // 7. North arrow
        canvas.drawText("N", 40f, 50f, compassText)
        canvas.drawLine(40f, 60f, 40f, 100f, compassPaint)
        canvas.drawLine(30f, 80f, 40f,  60f, compassPaint)
        canvas.drawLine(50f, 80f, 40f,  60f, compassPaint)
    }
}