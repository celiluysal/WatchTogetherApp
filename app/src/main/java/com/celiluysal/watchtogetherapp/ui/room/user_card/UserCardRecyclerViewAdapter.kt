package com.celiluysal.watchtogetherapp.ui.room.user_card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.celiluysal.watchtogetherapp.databinding.ItemAvatarCenteredBinding
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.models.WTVideo
import com.celiluysal.watchtogetherapp.utils.WTUtils

class UserCardRecyclerViewAdapter(val wtUsers: MutableList<WTUser>, val clickListener: onUserCardClickListener):
RecyclerView.Adapter<UserCardRecyclerViewAdapter.UserViewHolder>(){

    class UserViewHolder(val context: Context, val binding: ItemAvatarCenteredBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(wtUser: WTUser, action: onUserCardClickListener) {
                binding.imageViewAvatar.setImageResource(
                    WTUtils.shared.getAvatarResId(
                        context, wtUser.avatarId
                    )
                )

                binding.relativeLayoutAvatar.setOnClickListener {
                    action.onUserCardClick()

                }
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
        return holder.bind(wtUsers[position], clickListener)
    }

    override fun getItemCount(): Int {
        return if (wtUsers.size > 4) 4 else wtUsers.size
    }

    interface onUserCardClickListener {
        fun onUserCardClick()
    }
}