package com.celiluysal.watchtogetherapp.ui.room.playlist_dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.celiluysal.watchtogetherapp.databinding.ItemVideoCardBinding
import com.celiluysal.watchtogetherapp.models.WTVideo

class VideoRecyclerViewAdapter(
    private val playList: MutableList<WTVideo>,
    private val userIsOwner: Boolean,
    var clickListener: onVideoItemClickListener
) : RecyclerView.Adapter<VideoRecyclerViewAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(val binding: ItemVideoCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wtVideo: WTVideo, action: onVideoItemClickListener) {

            binding.textViewVideoTitle.text = wtVideo.title
            binding.textViewChannel.text = wtVideo.channel

            Glide.with(binding.root).load(wtVideo.thumbnail)
                .into(binding.imageViewThumbnail)

            if (userIsOwner) {
                binding.imageViewDelete.setOnClickListener {
                    action.onDeleteClick(wtVideo, adapterPosition)
                }

                binding.cardViewVideo.setOnClickListener{
                    action.onVideoItemClick(wtVideo, adapterPosition)
                }
            } else {
                binding.imageViewDelete.visibility = ImageView.GONE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        return holder.bind(playList[position], clickListener)
    }

    override fun getItemCount(): Int {
        return playList.size
    }

    interface onVideoItemClickListener {
        fun onVideoItemClick(item: WTVideo, position: Int)
        fun onDeleteClick(item: WTVideo, position: Int)
    }

}