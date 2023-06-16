package com.emre.weatherapi.service

import com.emre.weatherapi.model.WeatherConditions
import com.emre.weatherapi.model.WeatherData
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
interface WeatherAPI {

    /*
    Conditions Url
    https://www.weatherapi.com/docs/conditions.json

    Current Url
    http://api.weatherapi.com/v1/current.json?key=2b033bcd0adc4357970204628231306&q=istanbul&aqi=no
    */


    @GET("docs/conditions.json")
    fun getCondition(): Observable<List<WeatherConditions>>

    @GET("current.json?key=e30943b963ab48d09c471003231606&q=Ankara")
    fun getCurrent(): Observable<WeatherData>

    //@GET("current.json")
    //fun getCurrent(@Query("key") key: String, @Query("q") location: String): Observable<WeatherData>
}

