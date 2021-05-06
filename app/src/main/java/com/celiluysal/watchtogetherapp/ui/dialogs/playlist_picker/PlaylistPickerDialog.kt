package com.celiluysal.watchtogetherapp.ui.dialogs.playlist_picker

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.celiluysal.watchtogetherapp.databinding.DialogPlaylistBinding
import com.celiluysal.watchtogetherapp.models.WTVideo
import com.celiluysal.watchtogetherapp.ui.room.RoomViewModel
import com.celiluysal.watchtogetherapp.ui.search.SearchActivity


class PlaylistPickerDialog () :
    DialogFragment(), VideoRecyclerViewAdapter.onVideoItemClickListener {
    private lateinit var binding: DialogPlaylistBinding
    private lateinit var videoRecyclerViewAdapter: VideoRecyclerViewAdapter
    private lateinit var selectedItem: WTVideo
    private val viewModel: RoomViewModel by activityViewModels()

    companion object {
        private const val REQ_SEARCH = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogPlaylistBinding.inflate(layoutInflater)

        viewModel.wtRoom.observe(viewLifecycleOwner, {
            Log.e("PlaylistPickerDialog", "wtRoom observe")
        })

        binding.viewAdd.setOnClickListener {
            startActivityForResult(Intent(context, SearchActivity::class.java), REQ_SEARCH)
        }

        binding.imageViewClose.setOnClickListener {
            dialog?.dismiss()
        }

        fill()

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_SEARCH) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data?.getStringExtra("videoId")
                result?.let {
                    Log.e("PlaylistPickerDialog", it)
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
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