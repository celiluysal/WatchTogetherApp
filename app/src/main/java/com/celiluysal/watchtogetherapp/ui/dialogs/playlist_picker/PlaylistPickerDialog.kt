package com.celiluysal.watchtogetherapp.ui.dialogs.playlist_picker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.celiluysal.watchtogetherapp.databinding.DialogAvatarPickerBinding
import com.celiluysal.watchtogetherapp.databinding.DialogPlaylistBinding
import com.celiluysal.watchtogetherapp.models.AvatarImage
import com.celiluysal.watchtogetherapp.models.WTVideo
import com.celiluysal.watchtogetherapp.ui.dialogs.avatar_picker.AvatarRecyclerViewAdapter
import com.celiluysal.watchtogetherapp.ui.search.SearchActivity
import com.celiluysal.watchtogetherapp.utils.WTUtils

class PlaylistPickerDialog () :
    DialogFragment(), VideoRecyclerViewAdapter.onVideoItemClickListener {
    private lateinit var binding: DialogPlaylistBinding
    private lateinit var videoRecyclerViewAdapter: VideoRecyclerViewAdapter
    private lateinit var selectedItem: WTVideo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogPlaylistBinding.inflate(layoutInflater)

        binding.viewAdd.setOnClickListener {
            context?.startActivity(Intent(context, SearchActivity::class.java))
        }

        binding.imageViewClose.setOnClickListener {
            dialog?.dismiss()
        }

        fill()

        return binding.root
    }

    private fun fill() {

//        val list = WTUtils.shared.getAvatarList(activity?.baseContext)
//
//        binding.recyclerViewpPlaylist.layoutManager = LinearLayoutManager(activity)
//        videoRecyclerViewAdapter = VideoRecyclerViewAdapter(list, this)
//        binding.recyclerViewpPlaylist.adapter = videoRecyclerViewAdapter

    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.90).toInt()
        dialog!!.window?.setLayout(width, height)
    }

    override fun onVideoItemClick(item: WTVideo, position: Int) {
        TODO("Not yet implemented")
    }


}