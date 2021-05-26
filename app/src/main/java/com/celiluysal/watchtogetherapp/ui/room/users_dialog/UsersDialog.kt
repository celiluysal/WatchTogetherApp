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
import com.celiluysal.watchtogetherapp.ui.avatar_picker_dialog.AvatarPickerDialog
import com.celiluysal.watchtogetherapp.ui.message_dialog.MessageDialog
import com.celiluysal.watchtogetherapp.ui.room.RoomViewModel

class UsersDialog() : DialogFragment(), UserRecyclerViewAdapter.onUsersClickListener {
    private lateinit var binding: DialogUsersBinding
    private lateinit var userRecyclerViewAdapter: UserRecyclerViewAdapter
    private val viewModel: RoomViewModel by activityViewModels()
    private lateinit var messageDialog: MessageDialog

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
                    users, this
                )
                binding.recyclerViewUsers.adapter = userRecyclerViewAdapter
            }
        })
    }

    override fun onStart() {
        super.onStart()

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog!!.window?.setLayout(width, height)
    }

    override fun onDeleteUserClick(item: WTUser, position: Int) {
        messageDialog = MessageDialog(
            "Bu kişiyi odadan çıkarmak istediğinize emin misiniz?",
            object : MessageDialog.OnMessageDialogClickListener {
                override fun onLeftButtonClick() {
                    messageDialog.dismiss()
                }

                override fun onRightButtonClick() {
                    viewModel.kickFromRoom(item)
                    messageDialog.dismiss()
                }
            })

        messageDialog.leftButtonText = "İptal"
        messageDialog.rightButtonText = "Evet"
        activity?.supportFragmentManager?.let { messageDialog.show(it, "MessageDialog") }
    }
}