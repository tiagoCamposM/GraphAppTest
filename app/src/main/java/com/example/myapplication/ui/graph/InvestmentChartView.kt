package com.example.myapplication.ui.graph

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.myapplication.R

class InvestmentChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        // Margens do gráfico
        const val MARGIN_LEFT = 140f
        const val MARGIN_RIGHT = 60f
        const val MARGIN_TOP = 50f
        const val MARGIN_BOTTOM = 120f

        // Pontos e linhas
        const val POINT_RADIUS = 8f
        const val POINT_BORDER_WIDTH = 4f
        const val LINE_WIDTH = 4f

        // Grid e labels
        const val X_LABEL_STEPS = 5
        const val Y_LABEL_STEPS = 5
        const val Y_LABEL_MARGIN = 50f
        const val X_LABEL_OFFSET = 15f
        const val Y_LABEL_OFFSET = 50f

        // Crosshair
        const val CROSSHAIR_STROKE_WIDTH = 2f

        // Tooltip
        const val TOOLTIP_PADDING = 20f
        const val TOOLTIP_HEIGHT = 50f
        const val TOOLTIP_CORNER_RADIUS = 12f

        // Animações
        const val CHART_ANIMATION_DURATION = 3000L
        const val INITIAL_PROGRESS = 0f
    }

    private var chartData = ChartData(emptyList(), emptyList())
    private val paints = ChartPaints(context)
    private val linePath = Path()
    private var animationProgress = INITIAL_PROGRESS
    private var gridMode: Int = 2
    private var showPoints: Boolean = true
    private var paintGraphArea: Boolean = true
    private var selectedPoint: PointF? = null
    private var selectedXValue: Float? = null
    private var selectedYValue: Float? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.InvestmentChartView,
            0, 0
        ).apply {
            try {
                gridMode = getInt(R.styleable.InvestmentChartView_gridMode, 2)
                showPoints = getBoolean(R.styleable.InvestmentChartView_showPoints, true)
                paintGraphArea = getBoolean(R.styleable.InvestmentChartView_paintGraphArea, true)
            } finally {
                recycle()
            }
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                handleTouchAnywhere(event.x, event.y)
                performClick()
                return true
            }
            MotionEvent.ACTION_UP -> {
                selectedPoint = null
                selectedXValue = null
                selectedYValue = null
                invalidate()
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handleTouchAnywhere(x: Float, y: Float) {
        val chartLeft = MARGIN_LEFT
        val chartRight = width - MARGIN_RIGHT
        val chartTop = MARGIN_TOP
        val chartBottom = height - MARGIN_BOTTOM

        if (x < chartLeft || x > chartRight || y < chartTop || y > chartBottom) {
            selectedPoint = null
            selectedXValue = null
            selectedYValue = null
            invalidate()
            return
        }

        selectedPoint = PointF(x, y)

        val chartWidth = chartRight - chartLeft
        val chartHeight = chartBottom - chartTop

        val maxReturn = chartData.returns.maxOrNull() ?: 1f
        val minReturn = chartData.returns.minOrNull() ?: 0f
        val maxTime = chartData.timeValues.maxOrNull() ?: 1f
        val minTime = chartData.timeValues.minOrNull() ?: 0f

        selectedXValue = minTime + ((x - chartLeft) / chartWidth) * (maxTime - minTime)
        selectedYValue = minReturn + ((chartBottom - y) / chartHeight) * (maxReturn - minReturn)

        invalidate()
    }

    fun updateData(timeValues: List<Float>, returns: List<Float>) {
        chartData = ChartData(timeValues, returns)
        initializeScreenPoints()
        startChartAnimation()
    }

    private fun initializeScreenPoints() {
        chartData.screenPoints.apply {
            clear()
            repeat(chartData.timeValues.size) { add(PointF(INITIAL_PROGRESS, INITIAL_PROGRESS)) }
        }
    }

    private fun startChartAnimation(duration: Long = CHART_ANIMATION_DURATION) {
        ValueAnimator.ofFloat(INITIAL_PROGRESS, 1f).apply {
            this.duration = duration
            addUpdateListener { animationProgress = it.animatedValue as Float; invalidate() }
            start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        paints.areaPaint.shader = LinearGradient(
            0f, MARGIN_TOP, 0f, h - MARGIN_BOTTOM,
            paints.getAreaGradientStart(), paints.getAreaGradientEnd(),
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        updateScreenCoordinates()
        drawGrid(canvas)
        drawAxes(canvas)
        drawAxisLabels(canvas)
        drawLineAndPoints(canvas)
        selectedPoint?.let { drawCrosshair(canvas) }
    }

    private fun updateScreenCoordinates() {
        val chartWidth = width - MARGIN_LEFT - MARGIN_RIGHT
        val chartHeight = height - MARGIN_TOP - MARGIN_BOTTOM
        val xAxisY = height - MARGIN_BOTTOM

        val maxReturn = chartData.returns.maxOrNull() ?: 1f
        val minReturn = chartData.returns.minOrNull() ?: 0f
        val maxTime = chartData.timeValues.maxOrNull() ?: 1f
        val minTime = chartData.timeValues.minOrNull() ?: 0f

        chartData.timeValues.forEachIndexed { index, timeValue ->
            val x = MARGIN_LEFT + ((timeValue - minTime) / (maxTime - minTime).coerceAtLeast(1f) * chartWidth)
            val y = xAxisY - ((chartData.returns[index] - minReturn) / (maxReturn - minReturn).coerceAtLeast(1f) * chartHeight)
            chartData.screenPoints[index].set(x, y)
        }
    }

    private fun drawAxes(canvas: Canvas) {
        val xAxisY = height - MARGIN_BOTTOM
        canvas.drawLine(MARGIN_LEFT, MARGIN_TOP, MARGIN_LEFT, xAxisY, paints.axisPaint)
        canvas.drawLine(MARGIN_LEFT, xAxisY, width - MARGIN_RIGHT, xAxisY, paints.axisPaint)
    }

    private fun drawCrosshair(canvas: Canvas) {
        selectedPoint?.let { point ->
            paints.crosshairPaint.strokeWidth = CROSSHAIR_STROKE_WIDTH
            canvas.drawLine(point.x, MARGIN_TOP, point.x, height - MARGIN_BOTTOM, paints.crosshairPaint)
            canvas.drawLine(MARGIN_LEFT, point.y, width - MARGIN_RIGHT, point.y, paints.crosshairPaint)

            selectedXValue?.let { value ->
                val label = "X=%.1f".format(value)
                val textWidth = paints.textPaint.measureText(label)
                val rect = RectF(
                    point.x - textWidth / 2 - TOOLTIP_PADDING,
                    height - MARGIN_BOTTOM + TOOLTIP_PADDING,
                    point.x + textWidth / 2 + TOOLTIP_PADDING,
                    height - MARGIN_BOTTOM + TOOLTIP_PADDING + TOOLTIP_HEIGHT
                )
                canvas.drawRoundRect(rect, TOOLTIP_CORNER_RADIUS, TOOLTIP_CORNER_RADIUS, paints.tooltipBackgroundPaint)
                paints.textPaint.color = Color.WHITE
                canvas.drawText(label, rect.centerX() - textWidth / 2, rect.centerY() + 10f, paints.textPaint)
            }

            selectedYValue?.let { value ->
                val label = "Y=%.1f".format(value)
                val textWidth = paints.textPaint.measureText(label)
                val rect = RectF(
                    MARGIN_LEFT - textWidth - TOOLTIP_PADDING * 2,
                    point.y - TOOLTIP_HEIGHT / 2,
                    MARGIN_LEFT - TOOLTIP_PADDING,
                    point.y + TOOLTIP_HEIGHT / 2
                )
                canvas.drawRoundRect(rect, TOOLTIP_CORNER_RADIUS, TOOLTIP_CORNER_RADIUS, paints.tooltipBackgroundPaint)
                paints.textPaint.color = Color.WHITE
                canvas.drawText(label, rect.left + 10f, rect.centerY() + 10f, paints.textPaint)
            }
        }
    }

    private fun drawAxisLabels(canvas: Canvas) {
        val chartWidth = width - MARGIN_LEFT - MARGIN_RIGHT
        val chartHeight = height - MARGIN_TOP - MARGIN_BOTTOM
        val xAxisY = height - MARGIN_BOTTOM

        val maxReturn = chartData.returns.maxOrNull() ?: 1f
        val minReturn = chartData.returns.minOrNull() ?: 0f
        val yStepHeight = chartHeight / Y_LABEL_STEPS
        val yStepValue = (maxReturn - minReturn) / Y_LABEL_STEPS

        repeat(Y_LABEL_STEPS + 1) { i ->
            val y = xAxisY - yStepHeight * i
            val label = "%.0f".format(minReturn + yStepValue * i)
            canvas.drawText(
                label,
                MARGIN_LEFT - Y_LABEL_MARGIN - paints.axisTextPaint.measureText(label),
                y + paints.axisTextPaint.textSize / 2,
                paints.axisTextPaint
            )
        }

        val maxTime = chartData.timeValues.maxOrNull() ?: 1f
        val minTime = chartData.timeValues.minOrNull() ?: 0f
        val xStepWidth = chartWidth / X_LABEL_STEPS
        val xStepValue = (maxTime - minTime) / X_LABEL_STEPS

        repeat(X_LABEL_STEPS + 1) { i ->
            val x = MARGIN_LEFT + xStepWidth * i
            val label = "%.0f".format(minTime + xStepValue * i)
            canvas.drawText(label, x - X_LABEL_OFFSET, xAxisY + Y_LABEL_OFFSET, paints.axisTextPaint)
        }
    }

    private fun drawLineAndPoints(canvas: Canvas) {
        linePath.reset()
        val points = chartData.screenPoints
        if (points.size < 2) return

        val totalPoints = points.size
        val animatedIndex = (totalPoints - 1) * animationProgress
        val lastVisibleIndex = animatedIndex.toInt().coerceAtMost(totalPoints - 1)
        val subProgress = animatedIndex - lastVisibleIndex

        linePath.moveTo(points.first().x, points.first().y)

        for (i in 0 until lastVisibleIndex) {
            linePath.lineTo(points[i + 1].x, points[i + 1].y)
        }

        if (lastVisibleIndex < totalPoints - 1) {
            val start = points[lastVisibleIndex]
            val end = points[lastVisibleIndex + 1]
            val currentX = start.x + (end.x - start.x) * subProgress
            val currentY = start.y + (end.y - start.y) * subProgress
            linePath.lineTo(currentX, currentY)
        }

        if (paintGraphArea) {
            val lastPoint = if (lastVisibleIndex < totalPoints - 1) {
                val start = points[lastVisibleIndex]
                val end = points[lastVisibleIndex + 1]
                PointF(start.x + (end.x - start.x) * subProgress, start.y + (end.y - start.y) * subProgress)
            } else {
                points.last()
            }
            drawAreaChart(lastPoint, points, canvas)
        }

        canvas.drawPath(linePath, paints.linePaint)

        if (showPoints) drawPoints(lastVisibleIndex, points, totalPoints, subProgress, canvas)
    }

    private fun drawPoints(
        lastVisibleIndex: Int,
        points: MutableList<PointF>,
        totalPoints: Int,
        subProgress: Float,
        canvas: Canvas
    ) {
        for (i in 0..lastVisibleIndex) {
            val point = points[i]
            var radius = POINT_RADIUS
            if (i == lastVisibleIndex && lastVisibleIndex < totalPoints - 1) {
                radius = calculatePointRadius(POINT_RADIUS, subProgress)
            }

            paints.pointPaint.style = Paint.Style.FILL
            paints.pointPaint.color = paints.getPointInnerColor()
            canvas.drawCircle(point.x, point.y, radius, paints.pointPaint)

            paints.pointPaint.style = Paint.Style.STROKE
            paints.pointPaint.strokeWidth = POINT_BORDER_WIDTH
            paints.pointPaint.color = paints.getPointBorderColor()
            canvas.drawCircle(point.x, point.y, radius, paints.pointPaint)
        }
    }

    private fun drawAreaChart(lastPoint: PointF, points: MutableList<PointF>, canvas: Canvas) {
        val filledAreaPath = Path(linePath).apply {
            lineTo(lastPoint.x, height - MARGIN_BOTTOM)
            lineTo(points.first().x, height - MARGIN_BOTTOM)
            close()
        }
        canvas.drawPath(filledAreaPath, paints.areaPaint)
    }

    private fun drawGrid(canvas: Canvas) {
        if (gridMode == 0) return

        val chartWidth = width - MARGIN_LEFT - MARGIN_RIGHT
        val chartHeight = height - MARGIN_TOP - MARGIN_BOTTOM
        val xAxisY = height - MARGIN_BOTTOM

        val yStepHeight = chartHeight / Y_LABEL_STEPS
        repeat(Y_LABEL_STEPS + 1) { i ->
            val y = xAxisY - yStepHeight * i
            canvas.drawLine(MARGIN_LEFT, y, width - MARGIN_RIGHT, y, paints.gridPaint)
        }

        if (gridMode == 2) {
            val xStepWidth = chartWidth / X_LABEL_STEPS
            repeat(X_LABEL_STEPS + 1) { i ->
                val x = MARGIN_LEFT + xStepWidth * i
                canvas.drawLine(x, MARGIN_TOP, x, xAxisY, paints.gridPaint)
            }
        }
    }
}
