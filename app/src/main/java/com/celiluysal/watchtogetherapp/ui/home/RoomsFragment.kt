package com.celiluysal.watchtogetherapp.ui.home

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.celiluysal.watchtogetherapp.Firebase.FirebaseManager
import com.celiluysal.watchtogetherapp.base.BaseFragment
import com.celiluysal.watchtogetherapp.databinding.RoomsFragmentBinding
import com.celiluysal.watchtogetherapp.models.WTRoom
import com.celiluysal.watchtogetherapp.ui.room.RoomActivity

class RoomsFragment : BaseFragment<RoomsFragmentBinding, RoomsViewModel>(),
    RoomsRecyclerViewAdapter.OnRoomCardItemClickListener {

    companion object {
        fun newInstance() = RoomsFragment()
    }

    private lateinit var roomsRecyclerViewAdapter: RoomsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RoomsViewModel::class.java)

    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchRooms()
        observeViewModel()
        Log.e("RoomsFragment", "onStart")
    }

    fun observeViewModel(){

        viewModel.wtRooms.observe(viewLifecycleOwner, { wtRooms ->
            Log.e("RoomsFragment", "observeee")

            binding.recyclerViewRooms.layoutManager = LinearLayoutManager(context)
            roomsRecyclerViewAdapter = RoomsRecyclerViewAdapter(wtRooms, this)
            binding.recyclerViewRooms.adapter = roomsRecyclerViewAdapter
        })
    }

    override fun onRoomCardClick(item: WTRoom, position: Int) {
//        Log.e("RoomsFragment", "seçilen oda: "+item.roomName)
//        Toast.makeText(activity, "seçilen oda: "+item.roomName, Toast.LENGTH_SHORT).show()

        viewModel.joinRoom(item.roomId, null)
        viewModel.wtRoomId.observe(viewLifecycleOwner, { roomId ->
//            Log.e("RoomsFragment", " oda: "+item.roomName)
            activity?.let {
//                Log.e("RoomsFragment", " oda: "+item.roomName)
                val intent = Intent(context, RoomActivity::class.java)
                intent.putExtra("wtRoomId", roomId)
                it.startActivity(intent)
                it.finish()
                viewModel.wtRoomId.removeObservers(viewLifecycleOwner)
            }
        })

    }



    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): RoomsFragmentBinding {
        return RoomsFragmentBinding.inflate(inflater, container, false)
    }



}