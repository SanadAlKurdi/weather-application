package com.example.weatherapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.weatherapplication.weathers.WeatherRepository
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsActivity : AppCompatActivity() {
    lateinit var weatherRepo: WeatherRepository
    lateinit var cityHolder: TextView
    lateinit var forecastWeather: ListView
    lateinit var lineChart: LineChart
    lateinit var cityName: String
    var defaultCity: String = "Amman"
    lateinit var forecastWeatherArray: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        cityHolder = findViewById<TextView>(R.id.cityName)
        lineChart = findViewById<LineChart>(R.id.lineChart)
        forecastWeather = findViewById<ListView>(R.id.forecastWeather)
        cityName = intent.getStringExtra("Search") ?: defaultCity
        cityHolder.text = cityName
        forecastWeatherArray = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, forecastWeatherArray)
        forecastWeather.adapter = adapter

        weatherRepo = WeatherRepository()

        lifecycleScope.launch(Dispatchers.IO) {
            val forecastWeatherResponse = weatherRepo.forecastWeatherApi(cityName)

            forecastWeatherResponse.onSuccess { response ->
                val forecastData = response.body()
                val entries = mutableListOf<Entry>()
                val xAxisLabels = mutableListOf<String>()

                val forecasts =
                    forecastData?.list?.filterIndexed { index, _ -> index % 8 == 0 }?.take(3)
                forecasts?.forEachIndexed { index, forecast ->
                    val date = forecast?.dtTxt
                    val currentTemperature = forecast?.main?.temp
                    val maxTemperature = forecast?.main?.tempMax
                    val minTemperature = forecast?.main?.tempMin
                    val status = forecast?.weather?.firstOrNull()?.description
                    val windSpeed = forecast?.wind?.speed
                    val humidity = forecast?.main?.humidity
                    val dayFormat = SimpleDateFormat("EE", Locale.getDefault())
                    val dayName =
                        (forecast?.dt?.times(1000))?.toLong()
                            ?.let { dayFormat.format(Date(it)) }
                    xAxisLabels.add(dayName.toString())
                    forecast?.main?.temp?.toFloat()?.let {
                        entries.add(
                            Entry(
                                xAxisLabels.size.toFloat() - 1,
                                it
                            )
                        )
                    }
                    forecastWeatherArray.add(
                        "$dayName ||$date \nStatus: $status\nCurrent : $currentTemperature°C\nMax: $maxTemperature°C\nMin: $minTemperature°C\nWind: $windSpeed Km/h\nHumidity: $humidity%"
                    )
                }

                withContext(Dispatchers.Main) {
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
                    lineChart.invalidate()
                }
            }.onFailure {
                startActivity(Intent(this@DetailsActivity, SearchActivity::class.java))
                finish()
            }
        }
    }
}