package com.celiluysal.watchtogetherapp.ui.room

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.celiluysal.watchtogetherapp.base.BaseActivity
import com.celiluysal.watchtogetherapp.databinding.ActivityRoomBinding
import com.celiluysal.watchtogetherapp.models.WTMessage
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.ui.main.MainActivity
import com.celiluysal.watchtogetherapp.ui.room.playlist.PlaylistDialog
import com.celiluysal.watchtogetherapp.ui.room.user_card.UserCardRecyclerViewAdapter
import com.celiluysal.watchtogetherapp.ui.room.users.UsersDialog
import com.celiluysal.watchtogetherapp.utils.WTUtils
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo


class RoomActivity : BaseActivity<ActivityRoomBinding, RoomViewModel>() {

    private lateinit var chatRecyclerViewAdapter: ChatRecyclerViewAdapter
    private lateinit var userCardRecyclerViewAdapter: UserCardRecyclerViewAdapter
    private lateinit var playlistDialog: PlaylistDialog
    private lateinit var usersDialog: UsersDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(RoomViewModel::class.java)
        val roomId = intent.extras?.get("wtRoomId") as String
        viewModel.fetchUser()
        viewModel.fetchRoom(roomId)

        observeViewModel()

        keyboardSizeListener()

        playListButton()

        video()


        binding.imageViewSend.setOnClickListener {
            val text = binding.editTextMessage.text.toString()
            if (text.isNotEmpty())
                viewModel.addMessageToRoom(text)
            binding.editTextMessage.text.clear()
        }

    }

    private fun video() {
        lifecycle.addObserver(binding.youtubePlayer)

        binding.youtubePlayer.getPlayerUiController().showSeekBar(false)
        binding.youtubePlayer.getPlayerUiController().showPlayPauseButton(true)
        binding.youtubePlayer.getPlayerUiController().showMenuButton(false)
        binding.youtubePlayer.enableBackgroundPlayback(true)
        
        binding.youtubePlayer.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                Log.e("getWhenReady", "onYouTubePlayer")

//                youTubePlayer.loadOrCueVideo(lifecycle,"xY8-vKsJ6QI", 0f)
            }
        })

        binding.youtubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                Log.e("addListener", "onReady")
//                youTubePlayer.loadVideo("xY8-vKsJ6QI", 0f)
                youTubePlayer.cueVideo("xY8-vKsJ6QI", 0f)
                youTubePlayer.play()
            }

        })
    }


    private fun usersCard(wtUsers: MutableList<WTUser>) {
        binding.includeRoomUsers.recyclerViewAvatar.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        userCardRecyclerViewAdapter = UserCardRecyclerViewAdapter(
            wtUsers,
            object : UserCardRecyclerViewAdapter.onUserCardClickListener {
                override fun onUserCardClick() {
                    usersDialog = UsersDialog()
                    usersDialog.show(supportFragmentManager, "UsersDialog")
                }

            })
        binding.includeRoomUsers.recyclerViewAvatar.adapter = userCardRecyclerViewAdapter

        if (wtUsers.size > 4) {
            binding.includeRoomUsers.relativeLayoutMoreCount.visibility = RelativeLayout.VISIBLE
            binding.includeRoomUsers.textViewMoreCount.text = "+${wtUsers.size - 4}"
        } else
            binding.includeRoomUsers.relativeLayoutMoreCount.visibility = RelativeLayout.GONE

    }

    private fun playListButton() {
        binding.viewPlaylist.setOnClickListener {
            playlistDialog = PlaylistDialog()
            playlistDialog.show(supportFragmentManager, "PlaylistDialog")
        }
    }

    private fun keyboardSizeListener() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = binding.root.rootView.height - binding.root.height
            if (heightDiff > WTUtils.shared.dpToPx(this, 200f)) {
                binding.relativeLayoutTop.visibility = RelativeLayout.GONE
            } else {
                binding.relativeLayoutTop.visibility = RelativeLayout.VISIBLE
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun observeViewModel() {
        viewModel.wtRoom.observe(this, { wtRoom ->
            viewModel.observeUsers(wtRoom.roomId)
            viewModel.observeMessages(wtRoom.roomId)
            viewModel.observeDeleteRoom(wtRoom.roomId)
        })

        viewModel.didRoomDelete.observe(this, {
            if (it) startMainActivity()
            viewModel.didKickRoom.removeObservers(this)
        })

        viewModel.didLeaveRoom.observe(this, {
            if (it) startMainActivity()
            viewModel.didKickRoom.removeObservers(this)
        })

        viewModel.didKickRoom.observe(this, {
            if (it) startMainActivity()
        })


        viewModel.wtUser.observe(this, { user ->
            viewModel.wtAllUsers.observe(this, { users ->
                viewModel.wtUsers?.let { usersCard(it) }

                viewModel.wtMessages.observe(this, { messages ->

                    messages?.let { fillChat(messages, user, users) }

                })
            })
        })
    }

    private fun fillChat(
        messages: MutableList<WTMessage>,
        wtUser: WTUser,
        wtUsers: MutableList<WTUser>
    ) {
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)
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

    override fun onBackPressed() {
        if (viewModel.userIsOwner()) {
            viewModel.deleteRoom()
        } else {
            viewModel.leaveFromRoom()
        }
    }


    override fun getViewBinding(): ActivityRoomBinding {
        return ActivityRoomBinding.inflate(layoutInflater)
    }
}