package com.celiluysal.watchtogetherapp.ui.room

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.base.BaseActivity
import com.celiluysal.watchtogetherapp.base.Constant
import com.celiluysal.watchtogetherapp.databinding.ActivityRoomBinding
import com.celiluysal.watchtogetherapp.models.WTContent
import com.celiluysal.watchtogetherapp.models.WTMessage
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.ui.main.MainActivity
import com.celiluysal.watchtogetherapp.ui.message_dialog.MessageDialog
import com.celiluysal.watchtogetherapp.ui.room.chat.ChatRecyclerViewAdapter
import com.celiluysal.watchtogetherapp.ui.room.playlist_dialog.PlaylistDialog
import com.celiluysal.watchtogetherapp.ui.room.user_card.UserCardRecyclerViewAdapter
import com.celiluysal.watchtogetherapp.ui.room.users_dialog.UsersDialog
import com.celiluysal.watchtogetherapp.utils.WTUtils
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener


class RoomActivity : BaseActivity<ActivityRoomBinding, RoomViewModel>() {

    private lateinit var chatRecyclerViewAdapter: ChatRecyclerViewAdapter
    private lateinit var userCardRecyclerViewAdapter: UserCardRecyclerViewAdapter
    private lateinit var playlistDialog: PlaylistDialog
    private lateinit var usersDialog: UsersDialog
    private lateinit var messageDialog: MessageDialog

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
    private var youtubePlayerCurrentTime: Float? = null
    private var shouldSyncVideo = true

    inner class YoutubePlayerListener : AbstractYouTubePlayerListener() {
        override fun onReady(youTubePlayer: YouTubePlayer) {
            youtubePlayer = youTubePlayer
            observeViewModel()
            if (viewModel.userIsOwner())
                changeVideo()
            Log.e("addListener", "onReady")
        }

        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            youtubePlayerCurrentTime = second
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState
        ) {
            when (state) {
                PlayerConstants.PlayerState.ENDED -> {
                    if (viewModel.userIsOwner())
                        changeVideo()
                    viewModel.videoState.value = RoomViewModel.VideoState.ENDED
                }
                PlayerConstants.PlayerState.PAUSED -> {
                    viewModel.videoState.value = RoomViewModel.VideoState.PAUSED
                }
                PlayerConstants.PlayerState.PLAYING -> {
                    if (shouldSyncVideo) {
                        viewModel.autoSync()
                        shouldSyncVideo = false
                    }

                    viewModel.videoState.value = RoomViewModel.VideoState.PLAYING
                }
                else -> return
            }
            Log.e("onStateChange", state.name)
        }
    }

    private fun changeVideo() {
        viewModel.manageNextVideo { wtVideo, error ->
            if (wtVideo == null)
                return@manageNextVideo
            else {
                Log.e("changeVideo", wtVideo.title)
                playVideo(wtVideo.videoId, 0f)
                viewModel.addContentToRoom(WTContent(wtVideo, 0f, true))
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

    private fun controlVideoView() {
        if (viewModel.userIsOwner()) {

            binding.relativeLayoutControlIcons.postDelayed({
                binding.relativeLayoutControlIcons.visibility = RelativeLayout.INVISIBLE
            }, Constant.VIDEO_ICON_HIDE_DELAY)

            binding.relativeLayoutControlArea.setOnClickListener {
                Log.e("video", "clicked")
                binding.relativeLayoutControlIcons.visibility = RelativeLayout.VISIBLE
                binding.relativeLayoutControlIcons.postDelayed({
                    binding.relativeLayoutControlIcons.visibility = RelativeLayout.INVISIBLE
                }, Constant.VIDEO_ICON_HIDE_DELAY)
            }

            binding.imageViewPlayPause.setOnClickListener {
                viewModel.updateContentCurrentTimeAndIsPlaying(
                    viewModel.videoState.value != RoomViewModel.VideoState.PLAYING,
                    youtubePlayerCurrentTime ?: 0f
                )
//                when (viewModel.videoState.value) {
//                    RoomViewModel.VideoState.PLAYING -> {
//                        Log.e("controlVideoView", "başlat")
//                        viewModel.updateContentCurrentTimeAndIsPlaying(
//                            true,
//                            youtubePlayerCurrentTime ?: 0f
//                        )
//                    }
//                    RoomViewModel.VideoState.PAUSED -> {
//                        Log.e("controlVideoView", "durdur")
//                        viewModel.updateContentCurrentTimeAndIsPlaying(
//                            false,
//                            youtubePlayerCurrentTime ?: 0f
//                        )
//                    }
//                }
            }

            binding.imageViewSeekForward.setOnClickListener {
                youtubePlayerCurrentTime?.let { currentTime ->
                    viewModel.updateContentCurrentTimeAndIsPlaying(
                        viewModel.videoState.value == RoomViewModel.VideoState.PLAYING,
                        currentTime + 10f
                    )
                }
            }

            binding.imageViewSeekBackward.setOnClickListener {
                youtubePlayerCurrentTime?.let { currentTime ->
                    viewModel.updateContentCurrentTimeAndIsPlaying(
                        viewModel.videoState.value == RoomViewModel.VideoState.PLAYING,
                        currentTime - 10f
                    )
                }
            }

        } else {
            binding.relativeLayoutControlIcons.visibility = TextView.GONE
        }
    }

    private fun observeViewModel() {
        viewModel.wtRoom.observe(this, { wtRoom ->
            viewModel.observeUsers(wtRoom.roomId)
            viewModel.observeMessages(wtRoom.roomId)
            viewModel.observeDeleteRoom(wtRoom.roomId)

            viewModel.observeIsPlaying { isPlaying ->
                if (isPlaying)
                    youtubePlayer?.play()
                else
                    youtubePlayer?.pause()
            }

            viewModel.observeCurrentTime { currentTime ->
//                if (!viewModel.userIsOwner())
                youtubePlayer?.seekTo(currentTime)
            }

            viewModel.observeVideo { wtVideo, error ->
                Log.e("observeVideo", "wtVideo")
                playVideo(wtVideo.videoId, 0f)
            }

        })

        if (viewModel.userIsOwner()) {
            viewModel.observeAutoSync {
                if (youtubePlayerCurrentTime != null) {
                    Log.e("observeAutoSync", "update")
                    viewModel.updateContentCurrentTimeAndIsPlaying(
                        viewModel.videoState.value == RoomViewModel.VideoState.PLAYING,
                        youtubePlayerCurrentTime!!
                    )
                }
            }
        }

        viewModel.videoState.observe(this, {
            when (viewModel.videoState.value) {
                RoomViewModel.VideoState.PLAYING -> binding.imageViewPlayPause.setImageResource(R.drawable.ic_pause)
                RoomViewModel.VideoState.PAUSED -> binding.imageViewPlayPause.setImageResource(R.drawable.ic_play)
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
                controlVideoView()
                viewModel.wtMessages.observe(this, { messages ->
                    messages?.let { fillChat(messages, user, users) }
                })
            })
        })
    }

    var lastVideoId: String = ""
    private fun playVideo(videoId: String, startSeconds: Float) {
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
        messageDialog = MessageDialog(
            "Odadan ayrılmak istediğinize emin misiniz?",
            object : MessageDialog.OnMessageDialogClickListener {
                override fun onLeftButtonClick() {
                    messageDialog.dismiss()
                }

                override fun onRightButtonClick() {
                    if (viewModel.userIsOwner()) {
                        viewModel.deleteRoom()
                    } else {
                        viewModel.leaveFromRoom()
                    }
                }
            })
        messageDialog.leftButtonText = "İptal"
        messageDialog.rightButtonText = "Evet"
        messageDialog.show(supportFragmentManager, "MessageDialog")
    }

    override fun getViewBinding(): ActivityRoomBinding {
        return ActivityRoomBinding.inflate(layoutInflater)
    }
}