package com.celiluysal.watchtogetherapp.ui.room.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.celiluysal.watchtogetherapp.databinding.ItemVideoCardBinding
import com.celiluysal.watchtogetherapp.models.WTVideo

class VideoRecyclerViewAdapter(
    private val playList: MutableList<WTVideo>,
    var clickListener: onVideoItemClickListener
) : RecyclerView.Adapter<VideoRecyclerViewAdapter.VideoViewHolder>() {

    class VideoViewHolder(val binding: ItemVideoCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wtVideo: WTVideo, action: onVideoItemClickListener) {

            binding.textViewVideoTitle.text = wtVideo.title
//            binding.textViewVideoTitle.isSelected = true
            binding.textViewChannel.text = wtVideo.channel

            Glide.with(binding.root).load(wtVideo.thumbnail)
                .into(binding.imageViewThumbnail)

            binding.imageViewDelete.setOnClickListener {
                action.onDeleteClick(wtVideo, adapterPosition)
            }

            binding.cardViewVideo.setOnClickListener{
                action.onVideoItemClick(wtVideo, adapterPosition)
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