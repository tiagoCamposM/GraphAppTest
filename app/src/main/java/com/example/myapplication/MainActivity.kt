package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.ui.ddlwithsearch.SearchActivity
import com.example.myapplication.ui.graph.ChartActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val buttonGraph = findViewById<Button>(R.id.buttonGraph)

        buttonGraph.setOnClickListener {
            val intent = Intent(this, ChartActivity::class.java)
            startActivity(intent)
        }

        val inputSearch = findViewById<Button>(R.id.inputSearch)

        inputSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }
}