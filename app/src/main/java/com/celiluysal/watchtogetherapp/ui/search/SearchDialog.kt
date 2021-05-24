package com.celiluysal.watchtogetherapp.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.databinding.DialogSearchBinding

class SearchDialog(private val clickListener: SearchDialog.onVideoClickListener): DialogFragment() {
        private lateinit var binding: DialogSearchBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.search_dialog_background)
        binding = DialogSearchBinding.inflate(layoutInflater)

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

                        clickListener.onVideoClick(videoId)
                        dismiss()
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
            dialog?.onBackPressed()
        }

        return binding.root
    }


    interface onVideoClickListener {
        fun onVideoClick(videoId: String)
    }


    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
    }


}