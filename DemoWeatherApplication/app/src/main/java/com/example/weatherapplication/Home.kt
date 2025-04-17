package com.example.weatherapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapplication.weathers.utils.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class Home : AppCompatActivity(){
    private lateinit var cityName : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val searchButton = findViewById<ImageButton>(R.id.searchButton)
        val defaultCityDetails = findViewById<Button>(R.id.defaultCityDetails)
        cityName = intent.getStringExtra("Search") ?: "Amman"
        getCurrentWeather()

        searchButton.setOnClickListener {
            val intent = Intent(this, Search::class.java)
            startActivity(intent)
        }
        defaultCityDetails.setOnClickListener {
            val intent = Intent(this, Details::class.java)
            intent.putExtra("Search", "Amman")
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

        GlobalScope.launch(Dispatchers.IO) {
            val currentWeatherResponse = try {
                RetrofitInstance.api.getCurrentWeather(
                    cityName,
                    "metric",
                    getString(R.string.apiKey)
                )
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "app error ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "app error ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }
            if (currentWeatherResponse.isSuccessful && currentWeatherResponse.body() != null) {
                val response = currentWeatherResponse.body()
                withContext(Dispatchers.Main) {
                    city.text = cityName
                    currentWeatherStatus.text =
                        response?.weather?.get(0)?.main.toString()
                    currentTemperature.text =
                        "${response?.main?.temp.toString()} °C"
                    maxTemperature.text =
                        "${response?.main?.tempMax.toString()} °C"
                    minTemperature.text =
                        "${response?.main?.tempMin.toString()} °C"
                    windSpeed.text = response?.wind?.speed.toString()
                    humidityPercentage.text = response?.main?.humidity.toString()
                }
            }
        }

    }
}