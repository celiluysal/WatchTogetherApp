package com.celiluysal.watchtogetherapp.ui.search

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModel
import com.celiluysal.watchtogetherapp.base.BaseActivity
import com.celiluysal.watchtogetherapp.databinding.ActivitySearchBinding
import com.celiluysal.watchtogetherapp.ui.dialogs.playlist_picker.VideoRecyclerViewAdapter

class SearchActivity : BaseActivity<ActivitySearchBinding,SearchViewModel>() {

    private lateinit var videoRecyclerViewAdapter: VideoRecyclerViewAdapter

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.webViewYoutube.webViewClient = WebViewClient()
        binding.webViewYoutube.loadUrl("https://www.youtube.com")
        binding.webViewYoutube.settings.javaScriptEnabled = true


        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }




    }

    override fun onBackPressed() {
        if (binding.webViewYoutube.canGoBack())
            binding.webViewYoutube.goBack()
        else
            super.onBackPressed()
    }

    override fun getViewBinding(): ActivitySearchBinding {
        return ActivitySearchBinding.inflate(layoutInflater)
    }
}