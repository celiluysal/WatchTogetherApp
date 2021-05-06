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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.celiluysal.watchtogetherapp.databinding.DialogPlaylistBinding
import com.celiluysal.watchtogetherapp.models.WTVideo
import com.celiluysal.watchtogetherapp.ui.room.RoomViewModel
import com.celiluysal.watchtogetherapp.ui.search.SearchActivity


class PlaylistPickerDialog() :
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

        observeViewModel()


        binding.viewAdd.setOnClickListener {
            startActivityForResult(Intent(context, SearchActivity::class.java), REQ_SEARCH)
        }

        binding.imageViewClose.setOnClickListener {
            dialog?.dismiss()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_SEARCH) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data?.getStringExtra("videoId")
                result?.let { videoId ->
                    viewModel.wtRoom.value?.roomId?.let { roomId ->
                        viewModel.addVideoToPlaylist(roomId, videoId)
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private fun observeViewModel() {
        viewModel.wtRoom.value?.roomId?.let { roomId ->
            viewModel.observePlayList(roomId)
        }

        viewModel.wtPlaylist.observe(viewLifecycleOwner, { wtPlaylist ->
            if (wtPlaylist != null) {
                binding.recyclerViewpPlaylist.visibility = RecyclerView.VISIBLE
                binding.recyclerViewpPlaylist.layoutManager = LinearLayoutManager(activity)
                videoRecyclerViewAdapter = VideoRecyclerViewAdapter(wtPlaylist, this)
                binding.recyclerViewpPlaylist.adapter = videoRecyclerViewAdapter
            } else
                binding.recyclerViewpPlaylist.visibility = RecyclerView.INVISIBLE

        })
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.90).toInt()
        dialog!!.window?.setLayout(width, height)
    }

    override fun onVideoItemClick(item: WTVideo, position: Int) {

    }

    override fun onDeleteClick(item: WTVideo, position: Int) {
        viewModel.wtRoom.value?.roomId?.let { roomId ->
            viewModel.deleteVideoFromPlaylist(roomId, item.videoId)
        }
    }
}