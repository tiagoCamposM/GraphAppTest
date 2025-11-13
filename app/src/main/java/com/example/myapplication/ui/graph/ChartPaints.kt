package com.example.myapplication.ui.graph

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import com.example.myapplication.R

class ChartPaints(context: Context) {

    private val lineColor = ContextCompat.getColor(context, R.color.line_color)
    private val areaStart = ContextCompat.getColor(context, R.color.area_gradient_start)
    private val areaEnd = ContextCompat.getColor(context, R.color.area_gradient_end)
    private val pointBorderColor = ContextCompat.getColor(context, R.color.point_border_color)
    private val pointInnerColor = ContextCompat.getColor(context, R.color.point_inner_color)
    private val axisColor = ContextCompat.getColor(context, R.color.axis_color)
    private val gridColor = ContextCompat.getColor(context, R.color.grid_color)
    private val textColor = ContextCompat.getColor(context, R.color.text_color)

    val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = axisColor
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = lineColor
        strokeWidth = 8f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = gridColor
        strokeWidth = 1f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = 32f
    }

    val crosshairPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }

    val tooltipBackgroundPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    val axisTextPaint = Paint().apply {
        color = Color.DKGRAY
        textSize = 32f
        isAntiAlias = true
    }

    val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    val areaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    fun getLineColor() = lineColor
    fun getAreaGradientStart() = areaStart
    fun getAreaGradientEnd() = areaEnd
    fun getPointBorderColor() = pointBorderColor
    fun getPointInnerColor() = pointInnerColor
}
