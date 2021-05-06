package com.celiluysal.watchtogetherapp.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.*
import com.celiluysal.watchtogetherapp.base.BaseActivity
import com.celiluysal.watchtogetherapp.databinding.ActivitySearchBinding
import com.celiluysal.watchtogetherapp.ui.dialogs.playlist_picker.VideoRecyclerViewAdapter


class SearchActivity : BaseActivity<ActivitySearchBinding, SearchViewModel>() {

    private lateinit var videoRecyclerViewAdapter: VideoRecyclerViewAdapter

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.webViewYoutube.run {
            webViewClient = object: WebViewClient() {

                override fun doUpdateVisitedHistory(
                    view: WebView?,
                    url: String?,
                    isReload: Boolean
                ) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    Log.e("search update", url.toString())
                    if(url?.contains("watch?v=") == true) {
                        val videoId = url.split("watch?v=")[1].split("&")[0]
                        loadUrl("")

                        val returnIntent = Intent()
                        returnIntent.putExtra("videoId", videoId)
                        setResult(RESULT_OK, returnIntent)
                        finish()
                    }
                }
            }
            settings.javaScriptEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.domStorageEnabled = true
            loadUrl("https://www.youtube.com")
        }


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