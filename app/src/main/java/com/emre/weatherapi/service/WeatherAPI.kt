package com.emre.weatherapi.service

import com.emre.weatherapi.model.WeatherModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
interface WeatherAPI {


    // https://api.weatherbit.io/v2.0/current?lat=35.7796&lon=-78.6382&key=API_KEY

    @GET("current")
    fun getCurrent(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("key") key: String
    ): Observable<WeatherModel>

}

