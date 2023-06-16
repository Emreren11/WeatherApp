package com.emre.weatherapi.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.emre.weatherapi.R
import com.emre.weatherapi.databinding.ActivityMainBinding
import com.emre.weatherapi.model.WeatherConditions
import com.emre.weatherapi.model.WeatherData
import com.emre.weatherapi.service.WeatherAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val CONDITIONS_BASE ="https://www.weatherapi.com/"
    private val CURRENT_BASE ="https://api.weatherapi.com/v1/"
    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getWeather()


    }
//https://api.weatherapi.com/v1/current.json?key=2b033bcd0adc4357970204628231306&q=Ankara
    private fun getWeather() {

        val retrofit =  Retrofit.Builder()
            .baseUrl(CURRENT_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(WeatherAPI::class.java)

        val key = "2b033bcd0adc4357970204628231306"
        val location = "Ankara"
        compositeDisposable.add(
            retrofit.getCurrent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )

    }

    private fun handleResponse(current: WeatherData) {

        println(current.humidity)
    }
}