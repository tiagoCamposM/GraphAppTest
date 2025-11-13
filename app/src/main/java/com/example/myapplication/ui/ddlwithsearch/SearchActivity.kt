package com.example.myapplication.ui.ddlwithsearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.google.android.material.textfield.TextInputLayout

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        // Ajuste edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val paisDropdown = findViewById<SearchableDropdown<Country>>(R.id.dropdownPais)
        paisDropdown.setup(
            items = countryList,
            getName = { it.name },
            onItemSelected = { country ->
                Toast.makeText(this, "Selecionou: ${country.name}", Toast.LENGTH_SHORT).show()
            }
        )

        val nomesDropdown = findViewById<SearchableDropdown<Person>>(R.id.dropdownNomes)
        nomesDropdown.setup(
            items = peopleList,
            getName = { it.fullName },
           onItemSelected = {
                Toast.makeText(this, "Selecionou: ${it.fullName} - ${it.nickname}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
