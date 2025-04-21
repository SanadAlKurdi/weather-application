package com.example.weatherapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapplication.weathers.utils.RetrofitInstance
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class Details : AppCompatActivity() {
    lateinit var cityHolder: TextView
    lateinit var forecastWeather: ListView
    lateinit var lineChart: LineChart
    lateinit var cityName: String
    lateinit var forecastWeatherArray: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        cityHolder = findViewById<TextView>(R.id.cityName)
        lineChart = findViewById<LineChart>(R.id.lineChart)
        forecastWeather = findViewById<ListView>(R.id.forecastWeather)
        cityName = intent.getStringExtra("Search").toString()
        cityHolder.text = cityName
        forecastWeatherArray = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, forecastWeatherArray)
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
                val xAxisLabels = mutableListOf<String>()
                val entries = mutableListOf<Entry>()
                val response = forecastWeatherResponse.body()
                val forecasts =
                    response?.list?.filterIndexed { index, _ -> index % 8 == 0 }?.take(3)
                withContext(Dispatchers.Main) {
                    forecasts?.forEach { forecast ->
                        val date = forecast?.dtTxt
                        val currentTemperature = forecast?.main?.temp
                        val maxTemperature = forecast?.main?.tempMax
                        val minTemperature = forecast?.main?.tempMin
                        val status = forecast?.weather?.get(0)?.description
                        val windSpeed = forecast?.wind?.speed
                        val humidity = forecast?.main?.humidity
                        val dayFormat = SimpleDateFormat("EE", Locale.getDefault())
                        val dayName =
                            dayFormat.format(Date((forecast?.dt?.times(1000))?.toLong()!!))
                        xAxisLabels.add(dayName)
                        entries.add(
                            Entry(
                                xAxisLabels.size.toFloat() - 1,
                                forecast.main?.temp?.toFloat()!!
                            )
                        )
                        forecastWeatherArray.add(
                            "$date || $dayName \n Status: $status \n Current temperature: $currentTemperature°C \n Max temperature: $maxTemperature°C \n Min temperature: $minTemperature°C" +
                                    "\n Wind Speed: $windSpeed Km \n Humidity: $humidity%"
                        )
                    }
                    val formatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return if (index >= 0 && index < xAxisLabels.size) xAxisLabels[index] else ""
                        }
                    }
                    adapter.notifyDataSetChanged()
                    val data = LineDataSet(entries, "Forecast weather line chart")
                    val xAxis = lineChart.xAxis
                    xAxis.valueFormatter = formatter
                    xAxis.granularity = 1f
                    xAxis.setLabelCount(xAxisLabels.size, true)
                    xAxis.position = XAxis.XAxisPosition.TOP

                    data.color = R.color.morni_green
                    data.valueTextColor = Color.BLACK
                    data.lineWidth = 2f
                    data.circleRadius = 4f
                    data.setCircleColor(Color.GRAY)
                    data.mode = LineDataSet.Mode.CUBIC_BEZIER
                    val lineData = LineData(data)
                    lineChart.data = lineData
                }
            } else {
                withContext(Dispatchers.Main) {
                    startActivity(Intent(applicationContext, Search::class.java))
                    Toast.makeText(
                        applicationContext,
                        "The city name you entered not valid, try with another name",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }
    }
}