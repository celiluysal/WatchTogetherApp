package com.celiluysal.watchtogetherapp.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.celiluysal.watchtogetherapp.databinding.ItemRoomCardBinding
import com.celiluysal.watchtogetherapp.models.WTRoom

class RoomsRecyclerViewAdapter(
    val wtRooms: MutableList<WTRoom>,
    var clickListener: OnRoomCardItemClickListener
) : RecyclerView.Adapter<RoomsRecyclerViewAdapter.RoomViewHolder>() {

    class RoomViewHolder(val binding: ItemRoomCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(wtRoom: WTRoom, action: OnRoomCardItemClickListener) {

            binding.textViewRoomName.text = wtRoom.roomName
            binding.textViewPersonCount.text = wtRoom.users.size.toString()
            binding.textViewContent.isSelected = true

            itemView.setOnClickListener {
                Log.e("RoomsRecyclerView", " oda: "+wtRoom.roomName)
                action.onRoomCardClick(wtRoom, adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(wtRooms[position], clickListener)
    }

    override fun getItemCount(): Int {
        return wtRooms.size
    }

    interface OnRoomCardItemClickListener {
        fun onRoomCardClick(item: WTRoom, position: Int)
    }
}