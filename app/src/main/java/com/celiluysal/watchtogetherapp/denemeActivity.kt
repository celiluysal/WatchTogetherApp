package com.celiluysal.watchtogetherapp

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class denemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("ds", "denemeActivity onCreate")
        val config = Configuration()
        config.setToDefaults()
        Log.d("Config", config.toString())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deneme)
    }
}