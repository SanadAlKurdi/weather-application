package com.example.weatherapplication

import android.icu.util.Calendar
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
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
import java.text.SimpleDateFormat
import java.util.Date

class Details : AppCompatActivity() {
    lateinit var cityHolder: TextView
    lateinit var cityName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        cityHolder = findViewById<TextView>(R.id.cityName)

        cityName = intent.getStringExtra("Search").toString()
        cityHolder.text = cityName
        val date = Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MONTH, 1)
        val forecastWeather = findViewById<ListView>(R.id.forecastWeather)
        val forecastWeatherArray: ArrayList<String> = ArrayList()
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, forecastWeatherArray)
        forecastWeather.adapter = adapter
        GlobalScope.launch(Dispatchers.IO) {
            val forecastWeatherResponse = try {
                RetrofitInstance.api.getForecastWeather(
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
            if (forecastWeatherResponse.isSuccessful && forecastWeatherResponse.body() != null) {
                val response = forecastWeatherResponse.body()
                val forecasts =
                    response?.list?.filterIndexed { index, _ -> index % 8 == 0 }?.take(4)
                withContext(Dispatchers.Main) {
                    forecasts?.forEach { forecast ->
                        val date = forecast?.dtTxt
                        val currentTemperature = forecast?.main?.temp
                        val maxTemperature = forecast?.main?.tempMax
                        val minTemperature = forecast?.main?.tempMin
                        val status = forecast?.weather?.get(0)?.description
                        val windSpeed = forecast?.wind?.speed
                        val humidity = forecast?.main?.humidity

                        forecastWeatherArray.add("$date ||\t Status: $status \n Current temperature: $currentTemperature°C \n Max temperature: $maxTemperature°C \n Min temperature: $minTemperature°C" +
                                "\n Wind Speed: $windSpeed-Km \n Humidity: $humidity%")
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }


    }
}