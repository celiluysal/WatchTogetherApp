package com.celiluysal.watchtogetherapp.ui.room

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.celiluysal.watchtogetherapp.databinding.ItemAvatarCenteredBinding
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.utils.WTUtils

class UserCardRecyclerViewAdapter(val wtUsers: MutableList<WTUser>):
RecyclerView.Adapter<UserCardRecyclerViewAdapter.UserViewHolder>(){

    class UserViewHolder(val context: Context, val binding: ItemAvatarCenteredBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(wtUser: WTUser) {
                binding.imageViewAvatar.setImageResource(
                    WTUtils.shared.getAvatarResId(
                        context, wtUser.avatarId
                    )
                )
            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemAvatarCenteredBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return UserViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        return holder.bind(wtUsers[position])
    }

    override fun getItemCount(): Int {
        return if (wtUsers.size > 4) 4 else wtUsers.size
    }
}