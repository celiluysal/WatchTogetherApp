package com.celiluysal.watchtogetherapp.ui.room

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.celiluysal.watchtogetherapp.base.BaseActivity
import com.celiluysal.watchtogetherapp.databinding.ActivityRoomBinding
import com.celiluysal.watchtogetherapp.models.WTRoom
import com.celiluysal.watchtogetherapp.ui.login.LoginViewModel
import com.celiluysal.watchtogetherapp.utils.WTSessionManager

class RoomActivity : BaseActivity<ActivityRoomBinding, RoomViewModel>() {

    private lateinit var chatRecyclerViewAdapter: ChatRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(RoomViewModel::class.java)
        val roomId = intent.extras?.get("wtRoomId") as String
        viewModel.fetchRoom(roomId)


        observeRoomUsers()

        binding.imageViewSend.setOnClickListener {
            val text = binding.editTextMessage.text.toString()
            if (text.isNotEmpty())
                viewModel.addMessageToRoom(text)
            binding.editTextMessage.text.clear()
        }

    }


    private fun observeRoomUsers() {
        viewModel.wtUser.observe(this, Observer {
            viewModel.wtUsers.observe(this, { wtUsers ->
                observeRoom()
            })
        })
    }

    private fun observeRoom(){
        viewModel.wtRoom.observe(this, { wtRoom ->
            wtRoom.messages?.let { messages ->
                binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)
                chatRecyclerViewAdapter = ChatRecyclerViewAdapter(messages, viewModel.wtUser.value!!, viewModel.wtUsers.value!!)
                binding.recyclerViewChat.adapter = chatRecyclerViewAdapter
                binding.recyclerViewChat.scrollToPosition(messages.size-1)
                binding.recyclerViewChat.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                    binding.recyclerViewChat.scrollToPosition(messages.size-1)
                }
            }
        })
    }

    override fun getViewBinding(): ActivityRoomBinding {
        return ActivityRoomBinding.inflate(layoutInflater)
    }
}