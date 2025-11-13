package com.example.myapplication.ui.graph

import android.graphics.PointF

data class ChartData(
    val timeValues: List<Float>,
    val returns: List<Float>
) {
    val screenPoints: MutableList<PointF> = mutableListOf()
}
