package com.celiluysal.watchtogetherapp.ui.room

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.celiluysal.watchtogetherapp.base.BaseActivity
import com.celiluysal.watchtogetherapp.databinding.ActivityRoomBinding
import com.celiluysal.watchtogetherapp.models.WTRoom
import com.celiluysal.watchtogetherapp.ui.login.LoginViewModel

class RoomActivity : BaseActivity<ActivityRoomBinding, RoomViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(RoomViewModel::class.java)
        val roomId = intent.extras?.get("wtRoomId") as String
        viewModel.fetchRoom(roomId)

        viewModel.wtRoom.observe(this, { wtRoom ->
            var text = "mesaj yok"
            wtRoom.messages?.let { messages ->
                for (message in messages){
                    text = ""
                    text += message.ownerId + ":\n"
                    text += message.text + "\n\n"
                }
                binding.textViewInfo.text = text
            }

        })



        binding.imageViewSend.setOnClickListener {
            val text = binding.editTextMessage.text.toString()
            if (text.isNotEmpty())
                viewModel.addMessageToRoom(text)
        }



//        createRoomViewModel = ViewModelProvider(Main).get(CreateRoomViewModel::class.java)
//        createRoomViewModel.wtRoom.value

    }

    override fun getViewBinding(): ActivityRoomBinding {
        return ActivityRoomBinding.inflate(layoutInflater)
    }
}