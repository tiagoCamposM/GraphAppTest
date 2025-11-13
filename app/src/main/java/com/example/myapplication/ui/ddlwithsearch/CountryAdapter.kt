package com.example.myapplication.ui.ddlwithsearch

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R

class CountryAdapter(
    context: Context,
    private val countryList: List<Country>
) : ArrayAdapter<Country>(context, 0, countryList) {

    private var filteredList: List<Country> = countryList

    override fun getCount(): Int = filteredList.size

    override fun getItem(position: Int): Country = filteredList[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_dropdown, parent, false)
        val country = getItem(position)
        val textView = view.findViewById<TextView>(R.id.textViewCountry)
        textView.text = country.name
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase()?.trim() ?: ""
                val result = if (query.isEmpty()) {
                    countryList
                } else {
                    countryList.filter {
                        it.name.lowercase().contains(query) ||
                                it.abbreviation.lowercase().contains(query) ||
                                it.code.lowercase().contains(query)
                    }
                }
                return FilterResults().apply { values = result }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<Country> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}