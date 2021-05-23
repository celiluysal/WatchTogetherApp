package com.celiluysal.watchtogetherapp.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.celiluysal.watchtogetherapp.databinding.ItemRoomCardBinding
import com.celiluysal.watchtogetherapp.models.WTRoom

class RoomsRecyclerViewAdapter(
    private val wtRooms: MutableList<WTRoom>,
    var clickListener: OnRoomCardItemClickListener
) : RecyclerView.Adapter<RoomsRecyclerViewAdapter.RoomViewHolder>() {

    class RoomViewHolder(val binding: ItemRoomCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(wtRoom: WTRoom, action: OnRoomCardItemClickListener) {

            binding.chipRoomType.visibility = RelativeLayout.GONE

            binding.textViewRoomName.text = wtRoom.roomName
            binding.textViewPersonCount.text = wtRoom.users.size.toString()

            binding.textViewContent.text = wtRoom.content!!.video?.title
            binding.textViewContent.isSelected = true
            
            Glide.with(binding.root).load(wtRoom.content!!.video?.thumbnail)
                .into(binding.imageViewThumbnail)


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