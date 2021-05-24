package com.celiluysal.watchtogetherapp.ui.home

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.base.BaseFragment
import com.celiluysal.watchtogetherapp.databinding.RoomsFragmentBinding
import com.celiluysal.watchtogetherapp.models.WTRoom
import com.celiluysal.watchtogetherapp.ui.home.password_dialog.PasswordDialog
import com.celiluysal.watchtogetherapp.ui.room.RoomActivity

class RoomsFragment : BaseFragment<RoomsFragmentBinding, RoomsViewModel>(),
    RoomsRecyclerViewAdapter.OnRoomCardItemClickListener,
    PasswordDialog.onJoinButtonClickListener {

    companion object {
        fun newInstance() = RoomsFragment()
    }

    private lateinit var roomsRecyclerViewAdapter: RoomsRecyclerViewAdapter
    private lateinit var passwordDialog: PasswordDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.recyclerViewRooms.layoutManager = LinearLayoutManager(context)
        viewModel = ViewModelProvider(this).get(RoomsViewModel::class.java)
        viewModel.fetchRooms()
        observeViewModel()
        roomTypeTab()
        binding.textViewPublicTab.performClick()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.observeRoomsChild()

    }

    private fun roomTypeTab() {
        binding.textViewPublicTab.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                binding.textViewPublicTab.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.turquoise)
                binding.textViewPrivateTab.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.transparent)
                viewModel.roomType.value = RoomsViewModel.RoomType.PUBLIC
            }
        }
        binding.textViewPrivateTab.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                binding.textViewPublicTab.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.transparent)
                binding.textViewPrivateTab.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.turquoise)
                viewModel.roomType.value = RoomsViewModel.RoomType.PRIVATE
            }
        }
    }

    private fun observeViewModel() {
        viewModel.wtRooms.observe(viewLifecycleOwner, { wtRooms ->
            showRooms(wtRooms, viewModel.roomType.value)
        })

        viewModel.roomType.observe(viewLifecycleOwner, { roomType ->
            viewModel.wtRooms.value?.let {
                showRooms(it, roomType)
            }
        })

        viewModel.wtRoomId.observe(viewLifecycleOwner, { roomId ->
            activity?.let {
                val intent = Intent(context, RoomActivity::class.java)
                intent.putExtra("wtRoomId", roomId)
                it.startActivity(intent)
                it.finish()
                viewModel.wtRoomId.removeObservers(viewLifecycleOwner)
                viewModel.wtRooms.removeObservers(viewLifecycleOwner)
            }
        })
    }

    private fun showRooms(wtRooms: MutableList<WTRoom>, roomType: RoomsViewModel.RoomType?) {
        var rooms = wtRooms.filter { it.content != null } as MutableList
        rooms = (if (roomType == RoomsViewModel.RoomType.PUBLIC) rooms.filter { it.password == null }
            else rooms.filter { it.password != null }) as MutableList

        roomsRecyclerViewAdapter = RoomsRecyclerViewAdapter(rooms, this)
        binding.recyclerViewRooms.adapter = roomsRecyclerViewAdapter
    }

    override fun onRoomCardClick(wtRoom: WTRoom, position: Int) {
        if (wtRoom.password != null) {
            passwordDialog = PasswordDialog(wtRoom, this)
            passwordDialog.show(parentFragmentManager, "PasswordDialog")
        } else
            viewModel.joinRoom(wtRoom.roomId, null)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): RoomsFragmentBinding {
        return RoomsFragmentBinding.inflate(inflater, container, false)
    }


    override fun onJoinButtonClick(wtRoom: WTRoom?) {
        if (wtRoom != null) {
            viewModel.joinRoom(wtRoom.roomId, wtRoom.password)
            passwordDialog.dismiss()
        }
    }
}