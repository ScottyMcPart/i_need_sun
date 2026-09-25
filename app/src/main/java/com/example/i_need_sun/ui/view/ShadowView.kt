package com.example.i_need_sun.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.example.i_need_sun.R
import com.example.i_need_sun.domain.model.*
import com.example.i_need_sun.domain.solar.*

class ShadowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var scene: SceneConfig? = null
        set(value) { field = value; invalidate() }

    var minuteOfDay: Int = 720
        set(value) { field = value; invalidate() }

    private fun themeColor(attr: Int): Int {
        val tv = TypedValue()
        val ok = context.theme.resolveAttribute(attr, tv, true)
        if (!ok) {
            val night = resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK
            return if (night == android.content.res.Configuration.UI_MODE_NIGHT_YES)
                Color.parseColor("#2A3545") else Color.WHITE
        }
        return tv.data
    }

    private val groundPaint    get() = Paint().apply { color = themeColor(R.attr.colorGround); style = Paint.Style.FILL }
    private val shadowPaint    get() = Paint().apply {
        val night = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        color = Color.BLACK
        alpha = if (night == android.content.res.Configuration.UI_MODE_NIGHT_YES) 140 else 80
        style = Paint.Style.FILL; isAntiAlias = true
    }
    private val buildingFill   get() = Paint().apply { color = themeColor(R.attr.colorBuildingFill);    style = Paint.Style.FILL }
    private val buildingStroke get() = Paint().apply { color = themeColor(R.attr.colorBuildingOutline); style = Paint.Style.STROKE; strokeWidth = 2f; isAntiAlias = true }
    private val compassPaint   get() = Paint().apply { color = themeColor(R.attr.colorCompass);         strokeWidth = 2f; isAntiAlias = true }
    private val compassText    get() = Paint().apply { color = themeColor(R.attr.colorCompass);         textSize = 28f; textAlign = Paint.Align.CENTER; isAntiAlias = true }
    private val labelPaint     get() = Paint().apply { color = themeColor(R.attr.colorCompass);         textSize = 22f; textAlign = Paint.Align.CENTER; isAntiAlias = true }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cfg = scene ?: return

        val centreX = width / 2
        val centreY = height / 2

        // 1. Background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), groundPaint)

        // 2. Sun for this scene's latitude
        val sun = sunPosition(minuteOfDay, cfg.observer.geo.lat)

        // 3. Build pixel rects for every building
        val buildingRects = cfg.buildings.map { b ->
            Pair(
                b,
                buildingRectFromGeo(
                    observer      = cfg.observer.geo,
                    buildingGeo   = b.geo,
                    widthM        = b.widthM,
                    depthM        = b.depthM,
                    canvasCentreX = centreX,
                    canvasCentreY = centreY
                )
            )
        }

        // 4. Draw all shadow polygons first (painter's order)
        for ((building, rect) in buildingRects) {
            val sv = shadowVector(sun, building.heightM) ?: continue
            val pts  = shadowPolygon(rect, sv)
            val path = Path()
            path.moveTo(pts[0].x, pts[0].y)
            for (i in 1 until pts.size) path.lineTo(pts[i].x, pts[i].y)
            path.close()
            canvas.drawPath(path, shadowPaint)
        }

        // 5. Observer area — check if ANY building casts shadow on it
        val observerRect = observerRectFromGeo(
            observer      = cfg.observer,
            canvasCentreX = centreX,
            canvasCentreY = centreY
        )
        val inShadow = buildingRects.any { (building, rect) ->
            val sv = shadowVector(sun, building.heightM) ?: return@any false
            isPointInShadow(centreX.toFloat(), centreY.toFloat(), rect, sv)
        }

        val observerFill = Paint().apply {
            color = if (inShadow) Color.parseColor("#5B7FA6")
            else          Color.parseColor("#6AAF6A")
            style = Paint.Style.FILL; isAntiAlias = true
        }
        val observerStroke = Paint().apply {
            color = if (inShadow) Color.parseColor("#3A5F86")
            else          Color.parseColor("#3A8F3A")
            style = Paint.Style.STROKE; strokeWidth = 2f; isAntiAlias = true
        }
        canvas.drawRect(
            observerRect.x.toFloat(), observerRect.y.toFloat(),
            (observerRect.x + observerRect.wPx).toFloat(),
            (observerRect.y + observerRect.hPx).toFloat(),
            observerFill
        )
        canvas.drawRect(
            observerRect.x.toFloat(), observerRect.y.toFloat(),
            (observerRect.x + observerRect.wPx).toFloat(),
            (observerRect.y + observerRect.hPx).toFloat(),
            observerStroke
        )

        // 6. Draw all buildings on top of shadows
        for ((building, rect) in buildingRects) {
            val l   = rect.x.toFloat()
            val t   = rect.y.toFloat()
            val r   = (rect.x + rect.wPx).toFloat()
            val btm = (rect.y + rect.hPx).toFloat()
            canvas.drawRect(l, t, r, btm, buildingFill)
            canvas.drawRect(l, t, r, btm, buildingStroke)
            // Label
            if (building.label.isNotBlank()) {
                canvas.drawText(building.label, (l + r) / 2, t - 8f, labelPaint)
            }
        }

        // 7. North arrow
        canvas.drawText("N", 40f, 50f, compassText)
        canvas.drawLine(40f, 60f, 40f, 100f, compassPaint)
        canvas.drawLine(30f, 80f, 40f,  60f, compassPaint)
        canvas.drawLine(50f, 80f, 40f,  60f, compassPaint)
    }
}