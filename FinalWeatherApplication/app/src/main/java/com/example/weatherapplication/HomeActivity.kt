package com.example.weatherapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.weatherapplication.weathers.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
    lateinit var cityName: String
    var defaultCity: String = "Amman"
    lateinit var weatherRepo: WeatherRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val searchButton = findViewById<ImageButton>(R.id.searchButton)
        val defaultCityDetails = findViewById<Button>(R.id.defaultCityDetails)
        cityName = intent.getStringExtra("Search") ?: defaultCity
        weatherRepo = WeatherRepository()
        getCurrentWeather()

        searchButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        defaultCityDetails.setOnClickListener {
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("Search", defaultCity)
            startActivity(intent)
        }
    }

    private fun getCurrentWeather() {
        val currentTemperature = findViewById<TextView>(R.id.currentTemp)
        val city = findViewById<TextView>(R.id.cityName)
        val maxTemperature = findViewById<TextView>(R.id.maxTemp)
        val minTemperature = findViewById<TextView>(R.id.minTemp)
        val currentWeatherStatus = findViewById<TextView>(R.id.weatherStatus)
        val windSpeed = findViewById<TextView>(R.id.windSpeed)
        val humidityPercentage = findViewById<TextView>(R.id.humidityPercentage)

        lifecycleScope.launch(Dispatchers.IO) {
            val currentWeatherResponse = weatherRepo.currentWeatherApi(cityName)
            withContext(Dispatchers.Main) {
                currentWeatherResponse.onSuccess { response ->
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null) {
                        city.text = cityName
                        currentWeatherStatus.text = responseBody.weather?.get(0)?.main
                        currentTemperature.text = "${responseBody.main?.temp} °C"
                        maxTemperature.text = "${responseBody.main?.tempMax} °C"
                        minTemperature.text = "${responseBody.main?.tempMin} °C"
                        windSpeed.text = responseBody.wind?.speed?.toString()
                        humidityPercentage.text = responseBody.main?.humidity?.toString()
                    }
                }
            }
        }
    }
}