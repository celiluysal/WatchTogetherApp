package com.celiluysal.watchtogetherapp.ui.room.users

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.celiluysal.watchtogetherapp.databinding.ItemUserCardBinding
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.utils.WTUtils

class UserRecyclerViewAdapter(
    val roomOwnerId: String,
    val userId: String,
    val wtUsers: MutableList<WTUser>,
    var clickListener: onUsersClickListener
) :
    RecyclerView.Adapter<UserRecyclerViewAdapter.UserViewHolder>() {

    inner class UserViewHolder(val context: Context, val binding: ItemUserCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wtUser: WTUser, action: onUsersClickListener) {
            binding.imageViewAvatar.setImageResource(
                WTUtils.shared.getAvatarResId(
                    context,
                    wtUser.avatarId
                )
            )

            if (roomOwnerId == userId) {
                if (roomOwnerId == wtUser.userId){
                    binding.imageViewDelete.visibility = ImageView.INVISIBLE
                    binding.imageViewOwner.visibility = ImageView.VISIBLE
                } else {
                    binding.imageViewDelete.visibility = ImageView.VISIBLE
                    binding.imageViewOwner.visibility = ImageView.INVISIBLE

                }
            } else {
                if (roomOwnerId == wtUser.userId)
                    binding.imageViewOwner.visibility = ImageView.VISIBLE
                binding.imageViewDelete.visibility = ImageView.INVISIBLE
            }

            binding.textViewFullName.text = wtUser.fullName

            binding.imageViewDelete.setOnClickListener {
                action.onDeleteUserClick(wtUser, adapterPosition)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return UserViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        return holder.bind(wtUsers[position], clickListener)
    }

    override fun getItemCount(): Int {
        return wtUsers.size
    }

    interface onUsersClickListener {
        fun onDeleteUserClick(item: WTUser, position: Int)
    }

}