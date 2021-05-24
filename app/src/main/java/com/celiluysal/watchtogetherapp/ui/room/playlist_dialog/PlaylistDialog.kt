package com.celiluysal.watchtogetherapp.ui.room.playlist_dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.databinding.DialogPlaylistBinding
import com.celiluysal.watchtogetherapp.models.WTContent
import com.celiluysal.watchtogetherapp.models.WTVideo
import com.celiluysal.watchtogetherapp.ui.room.RoomViewModel
import com.celiluysal.watchtogetherapp.ui.search.SearchDialog


class PlaylistDialog() :
    DialogFragment(), VideoRecyclerViewAdapter.onVideoItemClickListener,
    SearchDialog.onVideoClickListener {
    private lateinit var binding: DialogPlaylistBinding
    private lateinit var videoRecyclerViewAdapter: VideoRecyclerViewAdapter
    private lateinit var searchDialog: SearchDialog
    private val viewModel: RoomViewModel by activityViewModels()

    companion object {
        private const val REQ_SEARCH = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        binding = DialogPlaylistBinding.inflate(layoutInflater)

        observeViewModel()

        if (viewModel.userIsOwner())
            binding.viewAdd.setOnClickListener {
                searchDialog = SearchDialog(this)
                searchDialog.show(parentFragmentManager, "SearchDialog")
            }
        else
            binding.relativeLayoutAdd.visibility = RelativeLayout.INVISIBLE

        binding.imageViewClose.setOnClickListener {
            dialog?.dismiss()
        }

        return binding.root
    }


    private fun observeViewModel() {
        viewModel.wtRoom.value?.roomId?.let { roomId ->
            viewModel.observePlayList(roomId)
        }

        viewModel.wtPlaylist.observe(viewLifecycleOwner, { wtPlaylist ->
            if (wtPlaylist != null) {
                binding.recyclerViewPlaylist.visibility = RecyclerView.VISIBLE
                binding.recyclerViewPlaylist.layoutManager = LinearLayoutManager(activity)
                videoRecyclerViewAdapter = VideoRecyclerViewAdapter(
                    wtPlaylist,
                    viewModel.userIsOwner(),
                    this
                )
                binding.recyclerViewPlaylist.adapter = videoRecyclerViewAdapter
            } else
                binding.recyclerViewPlaylist.visibility = RecyclerView.INVISIBLE

        })
    }

    override fun onStart() {
        super.onStart()

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog!!.window?.setLayout(width, height)

    }

    override fun onVideoItemClick(item: WTVideo, position: Int) {
        if (viewModel.userIsOwner())
            viewModel.addContentToRoom(WTContent(item, 0f, true))
    }

    override fun onDeleteClick(item: WTVideo, position: Int) {
        viewModel.wtRoom.value?.roomId?.let { roomId ->
            viewModel.deleteVideoFromPlaylist(roomId, item.videoId)
        }
    }

    override fun onVideoClick(videoId: String) {
        viewModel.wtRoom.value?.roomId?.let { roomId ->
            viewModel.addVideoToPlaylist(roomId, videoId)
        }
    }
}