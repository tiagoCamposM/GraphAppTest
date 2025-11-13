package com.example.myapplication.ui.graph

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R

class ChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val chartView = findViewById<InvestmentChartView>(R.id.chartView)


        val timeValues = (0..10).map { it.toFloat() }
        // val returns = timeValues.map { (0..100).random().toFloat() }
        val returns = timeValues.map { it*it }
        chartView.updateData(timeValues, returns)
    }
}