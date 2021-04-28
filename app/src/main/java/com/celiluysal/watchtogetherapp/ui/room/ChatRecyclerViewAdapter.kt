package com.celiluysal.watchtogetherapp.ui.room

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.celiluysal.watchtogetherapp.databinding.ItemMessageFromBinding
import com.celiluysal.watchtogetherapp.databinding.ItemMessageToBinding
import com.celiluysal.watchtogetherapp.models.WTMessage

class ChatRecyclerViewAdapter(val messages: MutableList<WTMessage>, val wtUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_TO = 0
        const val VIEW_TYPE_FROM = 1
    }

    private inner class MessageFromViewHolder(val binding: ItemMessageFromBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(wtMessage: WTMessage) {
            binding.textViewMessage.text = wtMessage.text
        }
    }

    private inner class MessageToViewHolder(val binding: ItemMessageToBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(wtMessage: WTMessage) {
            binding.textViewMessage.text = wtMessage.text
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].ownerId == wtUserId)
            VIEW_TYPE_TO
        else
            VIEW_TYPE_FROM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_TO)
            MessageToViewHolder(
                ItemMessageToBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        else
            MessageFromViewHolder(
                ItemMessageFromBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (messages[position].ownerId == wtUserId)
            (holder as MessageToViewHolder).bind(messages[position])
        else
            (holder as MessageFromViewHolder).bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}