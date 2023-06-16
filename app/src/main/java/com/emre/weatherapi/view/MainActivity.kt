package com.emre.weatherapi.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.PermissionRequest
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.emre.weatherapi.databinding.ActivityMainBinding
import com.emre.weatherapi.model.WeatherModel
import com.emre.weatherapi.service.WeatherAPI
import com.google.android.material.snackbar.Snackbar
import com.google.gson.internal.LinkedTreeMap
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import java.util.Locale
import kotlin.math.min
import kotlin.time.toDuration

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val BASE_URL ="https://api.weatherbit.io/v2.0/"
    private var compositeDisposable = CompositeDisposable()
    private val key = "2bbdc9b8ff7c4c53a9f07d3cf329de5e"
    private var lat = "41.036670"
    private var lon = "28.900964"
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var calendar: Calendar
    private lateinit var location: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getDateAndTime()

        registerLauncher()

        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = LocationListener { location ->
            lat = location.latitude.toString()
            lon = location.longitude.toString()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            lastLocation?.let {
                lat = lastLocation.latitude.toString()
                lon = lastLocation.longitude.toString()

                getWeather()
            }
        } else {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(view, "Permission needed for location", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }).show()
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }


    }
    private fun getWeather() {

        val retrofit =  Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(WeatherAPI::class.java)


        compositeDisposable.add(
        retrofit.getCurrent(lat, lon, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )

    }

    private fun handleResponse(current: WeatherModel) {

        val hashMap = current.data[0]

        val temp = hashMap.get("temp").toString()
        val oldTimezone = hashMap.get("timezone").toString()
        val timezone = oldTimezone.replace("_", " ").replace("/", " / ")
        val weather = hashMap.get("weather") as LinkedTreeMap<*, *>
        val description = weather.get("description").toString()
        val appTemp = hashMap.get("app_temp").toString().toDouble().toInt()
        val windSpd = hashMap.get("wind_spd").toString()
        val clouds = hashMap.get("clouds").toString()
        val rh = hashMap.get("rh").toString()



        binding.tempText.text = "$temp°C"
        binding.locationText.text = timezone
        binding.descriptionText.text = description
        binding.pTempText.text = "Perceived Temperature $appTemp°C"
        binding.windValueText.text = windSpd
        binding.cloudValueText.text = "%$clouds"
        binding.humidityValueText.text = "%$rh"

    }

    private fun registerLauncher() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->

            if (result) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    lastLocation?.let {
                        lat = lastLocation.latitude.toString()
                        lon = lastLocation.longitude.toString()
                        getWeather()
                    }
                }
            } else {
                Toast.makeText(this@MainActivity, "Permission needed for location", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun getDateAndTime() {

        calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        var monthName = ""
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        when(month) {
            1 ->
                monthName = "January"
            2 ->
                monthName = "February"
            3 ->
                monthName = "March"
            4 ->
                monthName = "April"
            5 ->
                monthName = "May"
            6 ->
                monthName = "June"
            7 ->
                monthName = "July"
            8 ->
                monthName = "August"
            9 ->
                monthName = "September"
            10 ->
                monthName = "October"
            11 ->
                monthName = "September"
            12 ->
                monthName = "December"
            else ->
                println("Error")
        }

        val formattedMinute: String
        val formattedHour: String
        if (minute < 10) {
            formattedMinute = "0$minute"
        } else {
            formattedMinute = minute.toString()
        }

        if (hour < 10) {
            formattedHour = "0$hour"
        } else {
            formattedHour = hour.toString()
        }

        binding.timeText.text = "$day/$month/$year, $formattedHour:$formattedMinute"
    }
}