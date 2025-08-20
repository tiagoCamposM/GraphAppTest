package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val chartView = findViewById<InvestmentChartView>(R.id.chartView)


        val timeValues = (0..10).map { it.toFloat() }
       // val returns = timeValues.map { (0..100).random().toFloat() }
        val returns = timeValues.map { it*it }
        chartView.updateData(timeValues, returns)
    }
}