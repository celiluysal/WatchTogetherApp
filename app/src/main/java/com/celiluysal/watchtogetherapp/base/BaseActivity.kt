package com.celiluysal.watchtogetherapp.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB: ViewBinding, VM: ViewModel>: AppCompatActivity() {
    protected lateinit var binding: VB
    protected lateinit var viewModel: VM
//    protected val adRequest: AdRequest = AdRequest.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setContentView(binding.root)

//        MobileAds.initialize(this) {}
    }

    protected abstract fun getViewBinding(): VB
}