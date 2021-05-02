package com.celiluysal.watchtogetherapp.ui.room

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.celiluysal.watchtogetherapp.base.BaseActivity
import com.celiluysal.watchtogetherapp.databinding.ActivityRoomBinding
import com.celiluysal.watchtogetherapp.models.WTMessage
import com.celiluysal.watchtogetherapp.models.WTRoom
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.ui.login.LoginViewModel
import com.celiluysal.watchtogetherapp.ui.main.MainActivity
import com.celiluysal.watchtogetherapp.utils.WTSessionManager

class RoomActivity : BaseActivity<ActivityRoomBinding, RoomViewModel>() {

    private lateinit var chatRecyclerViewAdapter: ChatRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(RoomViewModel::class.java)
        val roomId = intent.extras?.get("wtRoomId") as String
        viewModel.fetchUser()
        viewModel.fetchRoom(roomId)

        observeViewModel()



        binding.imageViewSend.setOnClickListener {
            val text = binding.editTextMessage.text.toString()
            if (text.isNotEmpty())
                viewModel.addMessageToRoom(text)
            binding.editTextMessage.text.clear()
        }

    }

    private fun observeViewModel() {
        viewModel.wtRoom.observe(this, { wtRoom ->
            viewModel.observeUsers(wtRoom.roomId)
            viewModel.observeMessages(wtRoom.roomId)
            viewModel.observeDeleteRoom(wtRoom.roomId)
        })

        viewModel.didRoomDelete.observe(this, {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        })

        viewModel.didLeaveRoom.observe(this, {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        })

        viewModel.wtUser.observe(this, { user ->
            viewModel.wtUsers.observe(this, { users ->
                viewModel.wtMessages.observe(this, { messages ->

                    messages?.let { fillChat(messages, user, users) }

                })
            })
        })
    }

    private fun fillChat(messages: MutableList<WTMessage>, wtUser: WTUser, wtUsers: MutableList<WTUser>) {
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)
        @Suppress("UNCHECKED_CAST")
        chatRecyclerViewAdapter = ChatRecyclerViewAdapter(
            messages,
            wtUser,
            wtUsers,
        )
        binding.recyclerViewChat.adapter = chatRecyclerViewAdapter

        binding.recyclerViewChat.scrollToPosition(messages.size - 1)
        binding.recyclerViewChat.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            binding.recyclerViewChat.scrollToPosition(messages.size - 1)
        }
    }


    inline fun <T : Any> ifLet(vararg elements: T?, closure: (List<T>) -> Unit) {
        if (elements.all { it != null }) {
            closure(elements.filterNotNull())
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()

        if (viewModel.userIsOwner()) {
            Log.e("RoomActivity", "owner - delete room")
            viewModel.deleteRoom()
        } else {
            Log.e("RoomActivity", "leave room")
            viewModel.leaveFromRoom()
        }
    }


    override fun getViewBinding(): ActivityRoomBinding {
        return ActivityRoomBinding.inflate(layoutInflater)
    }
}