package com.emre.weatherapi.model

data class WeatherConditions(
    val code: Int,
    val day: String,
    val night: String,
    val icon: Int,
    val languages: ArrayList<LanguageArray>
)

data class LanguageArray(
    val lang_name: String,
    val lang_iso: String,
    val day_text: String,
    val night_text: String
)

data class WeatherData(
    val last_updated: String,
    val wind_kph: Double,
    val humidity: Int,
    val feelslike_c: Double,
)

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)
