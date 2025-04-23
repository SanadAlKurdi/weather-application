package com.example.weatherapplication.weathers


import android.content.Context
import android.widget.Toast
import com.example.weatherapplication.weathers.current.CurrentWeatherApi
import com.example.weatherapplication.weathers.forecast.ForecastWeatherApi
import com.example.weatherapplication.weathers.utils.RetrofitInstance
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class WeatherRepository() {
    var apiKey = "b06c50f28e8e5841870067eb25cc00f1"
    var units: String = "metric"
    suspend fun currentWeatherApi(cityName: String, context: Context? = null): Result<Response<CurrentWeatherApi>> {
        return try {
            val response = RetrofitInstance.api.getCurrentWeather(cityName, units, apiKey)
            Result.success(response)
        } catch (e: IOException) {
            context?.showToast("No internet connection. Please check your network.")
            Result.failure(e)
        } catch (e: HttpException) {
            context?.showToast("City not found. Please try another name.")
            Result.failure(e)
        } catch (e: Exception) {
            context?.showToast("Something went wrong. Please try again.")
            Result.failure(e)
        }
    }

    suspend fun forecastWeatherApi(cityName: String, context: Context? = null): Result<Response<ForecastWeatherApi>> {
        return try {
            val response = RetrofitInstance.api.getForecastWeather(cityName, units, apiKey)
            Result.success(response)
        } catch (e: IOException) {
            context?.showToast("There is no internet connection")
            Result.failure(e)
        } catch (e: HttpException) {
            context?.showToast("City not found. Please try another name")
            Result.failure(e)
        } catch (e: Exception) {
            context?.showToast("Something went wrong. Please try again")
            Result.failure(e)
        }
    }

    private fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
