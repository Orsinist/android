package com.example.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

   // val CITY:String = "Казань"
    //val API: String = "427c7ed0fef839fa238e151352661339"
    val WEATHER_URL:String = "https://openweathermap.org/data/2.5/weather"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}