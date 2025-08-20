package com.example.myapplication

import android.graphics.PointF

data class ChartData(
    val timeValues: List<Float>,
    val returns: List<Float>
) {
    val screenPoints: MutableList<PointF> = mutableListOf()
}
