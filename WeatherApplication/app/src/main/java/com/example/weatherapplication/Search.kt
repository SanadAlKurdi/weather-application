package com.example.weatherapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class Search : AppCompatActivity(), AdapterView.OnItemClickListener {
    lateinit var preference: SharedPreferences
    lateinit var searchCity: EditText
    lateinit var searchButton: Button
    lateinit var historyDisplayAdapter: ListView
    lateinit var searchedCitiesPreferences: Set<String>
    lateinit var adapter: ArrayAdapter<String>
    lateinit var citiesArray: ArrayList<String>
    lateinit var enteredCity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        preference = getSharedPreferences("Search_History", MODE_PRIVATE)
        searchCity = findViewById<EditText>(R.id.cityEdit)
        searchButton = findViewById<Button>(R.id.searchCityButton)
        historyDisplayAdapter = findViewById<ListView>(R.id.searchHistory)
        searchedCitiesPreferences = emptySet()

        searchedCitiesPreferences =
            preference.getStringSet("searchedCities", emptySet()) ?: emptySet()
        citiesArray = ArrayList(searchedCitiesPreferences)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, citiesArray)
        historyDisplayAdapter.adapter = adapter

        searchButton.setOnClickListener {
            enteredCity = searchCity.text.toString()
            if (enteredCity.isBlank()) {
                Toast.makeText(this, "Please enter a city to search", Toast.LENGTH_SHORT).show()
            } else {
                if (!citiesArray.contains(enteredCity)) {
                    citiesArray.add(enteredCity)
                    preference.edit {
                        this.putStringSet(
                            "searchedCities",
                            citiesArray.toSet()
                        )
                    }
                    adapter.notifyDataSetChanged()
                }
                val intentObj = Intent(this, Details::class.java)
                intentObj.putExtra("Search", enteredCity)
                startActivity(intentObj)
            }
        }
        historyDisplayAdapter.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedCity = parent?.getItemAtPosition(position) as String
        val intentObj = Intent(this, Details::class.java)
        intentObj.putExtra("Search", selectedCity)
        startActivity(intentObj)
    }
}
