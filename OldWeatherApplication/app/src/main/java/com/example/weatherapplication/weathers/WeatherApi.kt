package com.example.weatherapplication.weathers

import com.example.weatherapplication.weathers.current.CurrentWeatherApi
import com.example.weatherapplication.weathers.forecast.ForecastWeatherApi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather?")
    suspend fun getCurrentWeather(
        @Query("q") city : String,
        @Query("units") units : String,
        @Query("appid") apiKey : String,
    ): Response<CurrentWeatherApi>

    @GET("forecast?")
    suspend fun getForecastWeather(
        @Query("q") city : String,
        @Query("units") units : String,
        @Query("appid") apiKey : String,
    ): Response<ForecastWeatherApi>
}