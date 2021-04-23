package com.celiluysal.watchtogetherapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.databinding.ActivityMainBinding
import com.celiluysal.watchtogetherapp.utils.WTSessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("ds", "MainActivity onCreate")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.main_host_fragment)
        binding.bottomNavBar.setupWithNavController(navController)

    }


}