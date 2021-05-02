package com.celiluysal.watchtogetherapp.ui.room

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.celiluysal.watchtogetherapp.databinding.ItemMessageFromBinding
import com.celiluysal.watchtogetherapp.databinding.ItemMessageToBinding
import com.celiluysal.watchtogetherapp.models.WTMessage
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.utils.WTUtils

class ChatRecyclerViewAdapter(
    val messages: MutableList<WTMessage>,
    val wtUser: WTUser,
    val wtUsers: MutableList<WTUser>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        Log.e("ChatRecyclerViewAdapter", "init")

    }

    companion object {
        const val VIEW_TYPE_TO = 0
        const val VIEW_TYPE_FROM = 1
    }

    private inner class MessageFromViewHolder(val context: Context, val binding: ItemMessageFromBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(wtMessage: WTMessage) {
            val messageOwner = (wtUsers.filter { it.userId == wtMessage.ownerId })[0]
            binding.imageViewAvatar.setImageResource(
                WTUtils.shared.getAvatarResId(
                    context,
                    messageOwner.avatarId
                )
            )
            val text = messageOwner.fullName.split(" ")[0] + ": " + wtMessage.text
            binding.textViewMessage.text = text
        }
    }

    private inner class MessageToViewHolder(val context: Context, val binding: ItemMessageToBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(wtMessage: WTMessage) {
            val messageOwner = (wtUsers.filter { it.userId == wtMessage.ownerId })[0]
            binding.imageViewAvatar.setImageResource(
                WTUtils.shared.getAvatarResId(
                    context,
                    messageOwner.avatarId
                )
            )
            binding.textViewMessage.text = wtMessage.text
        }
    }


    fun getAvatar() {

    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].ownerId == wtUser.userId)
            VIEW_TYPE_TO
        else
            VIEW_TYPE_FROM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_TO)
            MessageToViewHolder(
                parent.context,
                ItemMessageToBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        else
            MessageFromViewHolder(
                parent.context,
                ItemMessageFromBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (messages[position].ownerId == wtUser.userId)
            (holder as MessageToViewHolder).bind(messages[position])
        else
            (holder as MessageFromViewHolder).bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}