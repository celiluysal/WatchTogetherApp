package com.celiluysal.watchtogetherapp.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.celiluysal.watchtogetherapp.databinding.ItemAvatarBinding

class AvatarRecyclerViewAdapter(
    private val avatarImageList: ArrayList<AvatarImage>,
    var clickListener: onAvatarItemClickListener
) :
    RecyclerView.Adapter<AvatarRecyclerViewAdapter.AvatarViewHolder>() {

    class AvatarViewHolder(val binding: ItemAvatarBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(avatarImage: AvatarImage, action: onAvatarItemClickListener) {
            binding.imageViewAvatar.setImageResource(avatarImage.resource)
            binding.imageViewAvatar.setOnClickListener {
                action.onAvatarItemClick(avatarImage, adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val binding = ItemAvatarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return AvatarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        return holder.bind(avatarImageList[position], clickListener)
    }

    override fun getItemCount(): Int {
        return avatarImageList.size
    }

    interface onAvatarItemClickListener {
        fun onAvatarItemClick(item: AvatarImage, position: Int)
    }


}