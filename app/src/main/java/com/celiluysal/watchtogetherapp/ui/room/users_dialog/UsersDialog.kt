package com.celiluysal.watchtogetherapp.ui.room.users_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.databinding.DialogUsersBinding
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.ui.room.RoomViewModel

class UsersDialog() : DialogFragment(), UserRecyclerViewAdapter.onUsersClickListener {
    private lateinit var binding: DialogUsersBinding
    private lateinit var userRecyclerViewAdapter: UserRecyclerViewAdapter
    private val viewModel: RoomViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        binding = DialogUsersBinding.inflate(layoutInflater)

        observeViewModel()

        binding.relativeLayoutAdd.visibility = RelativeLayout.INVISIBLE
        binding.imageViewClose.setOnClickListener {
            dialog?.dismiss()
        }

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.wtAllUsers.observe(viewLifecycleOwner, {
            viewModel.wtUsers?.let { users ->
                binding.recyclerViewUsers.layoutManager = LinearLayoutManager(activity)
                userRecyclerViewAdapter = UserRecyclerViewAdapter(
                    viewModel.wtRoom.value!!.ownerId,
                    viewModel.wtUser.value!!.userId,
                    users, this)
                binding.recyclerViewUsers.adapter = userRecyclerViewAdapter
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.90).toInt()
        dialog!!.window?.setLayout(width, height)
    }

    override fun onDeleteUserClick(item: WTUser, position: Int) {
        viewModel.kickFromRoom(item)
    }
}