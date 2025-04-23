package com.example.weatherapplication.weathers.utils

import com.example.weatherapplication.weathers.WeatherApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api : WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(Util.Base)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}