package com.celiluysal.watchtogetherapp.ui.room

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.base.BaseActivity
import com.celiluysal.watchtogetherapp.databinding.ActivityRoomBinding
import com.celiluysal.watchtogetherapp.models.WTContent
import com.celiluysal.watchtogetherapp.models.WTMessage
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.ui.main.MainActivity
import com.celiluysal.watchtogetherapp.ui.room.playlist.PlaylistDialog
import com.celiluysal.watchtogetherapp.ui.room.user_card.UserCardRecyclerViewAdapter
import com.celiluysal.watchtogetherapp.ui.room.users.UsersDialog
import com.celiluysal.watchtogetherapp.utils.WTUtils
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue


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

        video()
        keyboardSizeListener()
        playListButton()

        binding.imageViewSend.setOnClickListener {
            val text = binding.editTextMessage.text.toString()
            if (text.isNotEmpty())
                viewModel.addMessageToRoom(text)
            binding.editTextMessage.text.clear()
        }
    }


    private fun video() {
        lifecycle.addObserver(binding.youtubePlayer)

        binding.youtubePlayer.run {
            getPlayerUiController().showSeekBar(false)
            getPlayerUiController().showPlayPauseButton(false)
            getPlayerUiController().showCurrentTime(true)
            addYouTubePlayerListener(YoutubePlayerListener())
        }
    }

    private var youtubePlayer: YouTubePlayer? = null
    private var youtubePlayerState: PlayerConstants.PlayerState? = null
    private var youtubePlayerCurrentTime: Float? = null


    inner class YoutubePlayerListener : AbstractYouTubePlayerListener() {
        override fun onReady(youTubePlayer: YouTubePlayer) {
            youtubePlayer = youTubePlayer
            observeViewModel()
            addContent()
            Log.e("addListener", "onReady")
        }

        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            youtubePlayerCurrentTime = second
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState
        ) {
            youtubePlayerState = state
            when (state) {
                PlayerConstants.PlayerState.ENDED -> {
                    addContent()
                    binding.textViewPlayPause.text = "Başlat"
                }
                PlayerConstants.PlayerState.PAUSED -> binding.textViewPlayPause.text = "Başlat"
                PlayerConstants.PlayerState.PLAYING -> binding.textViewPlayPause.text = "Durdur"
                else -> return
            }


            Log.e("onStateChange", state.name)
        }
    }

    private fun addContent() {
        viewModel.manageNextVideo() { wtVideo, error ->
            if (wtVideo == null)
                return@manageNextVideo
            else {
                viewModel.wtRoom.value?.roomId?.let { roomId ->
                    viewModel.addContentToRoom(roomId, WTContent(wtVideo, 0f, true))
                }
            }
        }
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
            viewModel.observeContent()
        })

        viewModel.wtContent.observe(this, { wtContent ->
            if (wtContent != null) {
                if (viewModel.userIsOwner())
                    ownerUserVideoFlow(wtContent)
                else
                    standardUserVideoFlow(wtContent)
            }
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

    private fun ownerUserVideoFlow(wtContent: WTContent) {
        if (lastVideoId != wtContent.video.videoId)
            playVideo(wtContent.video.videoId, 0f)

        if (wtContent.isPlaying) {
            youtubePlayer?.play()
        } else {
            youtubePlayer?.pause()
        }

        binding.textViewPlayPause.setOnClickListener {
            when (binding.textViewPlayPause.text) {
                "Başlat" -> {
                    viewModel.updateContentIsPlaying(true)
                    youtubePlayerCurrentTime?.let { currentTime ->
                        viewModel.updateContentCurrentTime(currentTime)
                    }
                }
                "Durdur" -> {
                    viewModel.updateContentIsPlaying(false)
                    youtubePlayerCurrentTime?.let { currentTime ->
                        viewModel.updateContentCurrentTime(currentTime)
                    }
                }
            }
        }

        viewModel.observeNewUser {
            youtubePlayerCurrentTime?.let { currentTime ->
                viewModel.updateContentCurrentTime(currentTime)
            }
        }
    }

    private fun standardUserVideoFlow(wtContent: WTContent) {
        val diff = (youtubePlayerCurrentTime?.minus(wtContent.currentTime))?.absoluteValue ?: 0f
        Log.e("diff", diff.toString())

        if (lastVideoId != wtContent.video.videoId){
            if (wtContent.isPlaying){
                if (diff > 3f)
                    playVideo(wtContent.video.videoId, wtContent.currentTime+4f)
                else
                    playVideo(wtContent.video.videoId, wtContent.currentTime)
            }
        }


        if (wtContent.isPlaying) {
            youtubePlayer?.play()
        } else {
            youtubePlayer?.pause()
        }



        binding.textViewPlayPause.visibility = TextView.GONE
        binding.youtubePlayer.isClickable = false
    }

    var lastVideoId: String = ""
    private fun playVideo(videoId: String, startSeconds: Float) {
        Log.e("playVideo", "videoId")
        youtubePlayer?.loadVideo(videoId, startSeconds)
        lastVideoId = videoId
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