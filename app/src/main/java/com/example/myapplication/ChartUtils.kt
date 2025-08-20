package com.example.myapplication

import android.graphics.PointF
import kotlin.math.PI
import kotlin.math.sin

fun interpolatePoints(p0: PointF, p1: PointF, progress: Float): PointF {
    return PointF(
        p0.x + (p1.x - p0.x) * progress,
        p0.y + (p1.y - p0.y) * progress
    )
}

fun calculatePointRadius(baseRadius: Float, subProgress: Float): Float {
    val bounce = sin(subProgress * PI).toFloat() * 0.3f + 0.7f
    return baseRadius * subProgress.coerceIn(0f, 1f) * bounce
}
