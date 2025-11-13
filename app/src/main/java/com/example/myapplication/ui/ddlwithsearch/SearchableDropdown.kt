package com.example.myapplication.ui.ddlwithsearch

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ArrayAdapter
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.R

class SearchableDropdown<T> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var textInputLayout: TextInputLayout
    private var autoCompleteTextView: MaterialAutoCompleteTextView
    private var selectedItem: T? = null
    private var threshold: Int = 2
    private var ignoreTextChanges = false

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_searchable_dropdown, this, true)

        textInputLayout = findViewById(R.id.textInputLayout)
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView)

        // Lê atributos customizados
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.SearchableDropdown, 0, 0)
            threshold = a.getInt(R.styleable.SearchableDropdown_threshold, threshold)
            val hintText = a.getString(R.styleable.SearchableDropdown_hint)
            hintText?.let { textInputLayout.hint = it }
            a.recycle()
        }

        autoCompleteTextView.threshold = threshold
    }

    fun setup(
        items: List<T>,
        getName: (T) -> String,
        onItemSelected: (T) -> Unit
    ) {
        val adapter = object : ArrayAdapter<String>(
            context,
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        ) {
            private var filteredItems: List<T> = items

            override fun getCount(): Int = filteredItems.size
            override fun getItem(position: Int): String = getName(filteredItems[position])

            override fun getFilter() = object : android.widget.Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val query = constraint?.toString()?.lowercase()?.trim() ?: ""
                    val resultsList: MutableList<T> = mutableListOf()

                    if (query.length >= threshold) {
                        for (item in items) {
                            val name = getName(item)
                            if (name.lowercase().contains(query)) {
                                resultsList.add(item)
                            }
                        }
                    }

                    return FilterResults().apply {
                        values = resultsList
                        count = resultsList.size
                    }
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    filteredItems = results?.values as? List<T> ?: emptyList()
                    clear()
                    addAll(filteredItems.map(getName))
                    notifyDataSetChanged()
                }
            }

            fun getItemAt(position: Int) = filteredItems[position]
        }

        autoCompleteTextView.setAdapter(adapter)

        // Seleção de item
        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            selectedItem = adapter.getItemAt(position)

            ignoreTextChanges = true
            autoCompleteTextView.setText(getName(selectedItem!!), false)
            autoCompleteTextView.dismissDropDown() // Fecha o dropdown corretamente
            ignoreTextChanges = false

            textInputLayout.error = null
            onItemSelected(selectedItem!!)
        }


        autoCompleteTextView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (autoCompleteTextView.text.length >= threshold) {
                    autoCompleteTextView.showDropDown()
                }
            }
            false // retorna false para permitir foco normal
        }

        // Validação ao perder foco
        autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val typedText = autoCompleteTextView.text.toString().trim()
                val found = items.find { getName(it).equals(typedText, ignoreCase = true) }
                if (found == null) {
                    textInputLayout.error = "Por favor, selecione um item válido"
                    selectedItem = null
                } else {
                    textInputLayout.error = null
                    selectedItem = found
                }
            } else {
                textInputLayout.error = null
            }
        }

        // TextWatcher
        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (ignoreTextChanges) return

                if (selectedItem?.let { getName(it).equals(s.toString(), ignoreCase = true) } == false) {
                    selectedItem = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun getSelectedItem(): T? = selectedItem
}
