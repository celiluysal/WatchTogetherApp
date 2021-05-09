package com.celiluysal.watchtogetherapp.ui.room

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.celiluysal.watchtogetherapp.models.*
import com.celiluysal.watchtogetherapp.network.Firebase.FirebaseManager
import com.celiluysal.watchtogetherapp.network.Youtube.ApiClient
import com.celiluysal.watchtogetherapp.network.Youtube.models.VideoDetail
import com.celiluysal.watchtogetherapp.utils.WTSessionManager
import com.celiluysal.watchtogetherapp.utils.WTUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RoomViewModel : ViewModel() {

    val wtRoom = MutableLiveData<WTRoom>()
    val wtUser = MutableLiveData<WTUser>()
    val wtAllUsers = MutableLiveData<MutableList<WTUser>>()
    val wtPlaylist = MutableLiveData<MutableList<WTVideo>>()
    val wtContent = MutableLiveData<WTContent>()
    val wtMessages = MutableLiveData<MutableList<WTMessage>?>()

    var wtUsers: MutableList<WTUser>? = null
    var wtOldUsers: MutableList<WTUser>? = null

    val didLeaveRoom = MutableLiveData<Boolean>()
    val didKickRoom = MutableLiveData<Boolean>()
    val didRoomDelete = MutableLiveData<Boolean>()

    val errorMessage = MutableLiveData<String>()
    val loadError = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading = MutableLiveData<Boolean>().apply { postValue(false) }


    private fun fetchVideoDetail(
        videoId: String,
        Result: ((wtVideo: WTVideo?, error: String?) -> Unit)
    ) {
        ApiClient.getClient.getVideoInfo(id = videoId)
            .enqueue(object : Callback<VideoDetail> {
                override fun onResponse(call: Call<VideoDetail>, response: Response<VideoDetail>) {
                    Log.e("getData", "onResponse")
                    val videoDetail = response.body()!!

                    videoDetail.items[0].snippet.run {
                        val wtVideo = WTVideo(
                            videoId = videoId,
                            title = title,
                            thumbnail = thumbnails.medium.url,
                            channel = channelTitle,
                            sendTime = WTUtils.shared.dayTimeStamp(),
                        )
                        Result.invoke(wtVideo, null)
                    }
                }

                override fun onFailure(call: Call<VideoDetail>, t: Throwable) {
                    Log.e("getData", "onFailure")
                    Result.invoke(null, t.localizedMessage)
                }
            })
    }

    fun addContentToRoom(roomId: String, wtContent: WTContent) {
        FirebaseManager.shared.addContentToRoom(
            roomId,
            wtContent
        ) { success, error ->
            if (success)
                Log.e("addContentToRoom", "success")
            else
                Log.e("addContentToRoom", error.toString())
        }
    }

    fun updateContentCurrentTime(currentTime: Float){
        wtRoom.value?.let { wtRoom ->
            FirebaseManager.shared.updateContentCurrentTime(wtRoom.roomId, currentTime) {success, error ->

            }
        }
    }

    fun updateContentIsPlaying(isPlaying: Boolean){
        wtRoom.value?.let { wtRoom ->
            FirebaseManager.shared.updateContentIsPlaying(wtRoom.roomId, isPlaying) {success, error ->  }
        }
    }

    fun manageNextVideo(Result: (wtVideo: WTVideo?, error: String?) -> Unit) {
        if (!userIsOwner())
            return

        FirebaseManager.shared.fetchPlaylist(wtRoom.value!!.roomId) { wtPlaylist, error ->
            if (wtPlaylist != null) {
                FirebaseManager.shared.fetchContent(wtRoom.value!!.roomId) { wtContent, error ->
                    if (wtContent != null) {
                        val videoId = wtContent.video.videoId
                        val currentIndex = wtPlaylist.indexOfFirst { it.videoId == videoId }
                        if (currentIndex + 1 == wtPlaylist.size)
                            Result.invoke(wtPlaylist.first(), null)
                        else
                            Result.invoke(wtPlaylist[currentIndex + 1], null)
                        Log.e("manageNextVideo", (currentIndex + 1).toString())
                    }
                }
            }


        }
    }

    fun observeContent() {
        FirebaseManager.shared.observeContent(wtRoom.value!!.roomId) { wtContent, error ->
            this.wtContent.value = wtContent
        }
    }

    fun observeNewUser(Result: () -> Unit) {
        wtRoom.value?.let { wtRoom ->
            FirebaseManager.shared.observeRoomUsersChild(wtRoom.roomId) {
                Log.e("observeNewUser", "success")
                Result.invoke()
            }
        }

    }

    fun addVideoToPlaylist(roomId: String, videoId: String) {
        fetchVideoDetail(videoId) { wtVideo, error ->
            if (wtVideo != null) {
                FirebaseManager.shared.addVideoToRoomPlaylist(roomId, wtVideo) { success, error ->
                    if (success) {
                        Log.e("addVideoToPlaylist", "success")
                        wtPlaylist.value?.let { playlist ->
                            if (playlist.size <= 1)
                                addContentToRoom(roomId, WTContent(wtVideo, 0f, true))
                        }
                    } else
                        Log.e("addVideoToPlaylist", error.toString())
                }
            }
        }
    }

    fun deleteVideoFromPlaylist(roomId: String, videoId: String) {
        FirebaseManager.shared.deleteVideoFromRoomPlaylist(roomId, videoId) { success, error ->
            if (success)
                Log.e("deleteVideoFromPlaylist", "success")
            else
                Log.e("deleteVideoFromPlaylist", error.toString())

        }
    }

    fun observePlayList(roomId: String) {
        FirebaseManager.shared.observePlaylist(roomId) { wtPlayList, error ->
            this.wtPlaylist.value = wtPlayList
        }
    }

    //    fun userIsOwner(): Boolean = wtRoom.value!!.ownerId == wtUser.value!!.userId
    fun userIsOwner(): Boolean {
        Log.e("userIsOwner", "owner-"+ wtRoom.value?.ownerId.toString())
        Log.e("userIsOwner", "user-"+ wtUser.value?.userId.toString())

        return wtRoom.value!!.ownerId == wtUser.value!!.userId
    }

    fun observeDeleteRoom(roomId: String) {
        FirebaseManager.shared.roomsRemoveObserver { wtRoom, error ->
            didRoomDelete.value = wtRoom.roomId == roomId
        }
    }

    fun deleteRoom() {
        FirebaseManager.shared.deleteRoom(wtRoom.value!!, wtUser.value!!) { success, error ->
        }
    }

    fun leaveFromRoom() {
        FirebaseManager.shared.leaveFromRoom(wtRoom.value!!, wtUser.value!!) { success, error ->
            didLeaveRoom.value = success
        }
    }

    fun kickFromRoom(wtUser: WTUser) {
        FirebaseManager.shared.leaveFromRoom(wtRoom.value!!, wtUser) { success, error -> }
    }

    fun fetchUser() {
        WTSessionManager.shared.user?.let { wtUser ->
            this.wtUser.value = wtUser
            Log.e("RoomViewModel", "chatModel.value?.wtUser")
        }
    }

    fun fetchRoom(roomId: String) {
        FirebaseManager.shared.fetchRoom(roomId) { wtRoom: WTRoom?, error: String? ->
            if (wtRoom != null) {
                Log.e("fetchRoom", wtRoom.roomName)
                this.wtRoom.value = wtRoom
            } else
                Log.e("fetchRoom", error!!)
        }
    }

    fun observeUsers(roomId: String) {
        FirebaseManager.shared.observeRoomUsers(roomId) { oldUsers, users, error ->
            users?.let { _users ->
                wtUser.value?.userId?.let { _userId ->
                    if (!isUserInRoom(_userId, _users))
                        didKickRoom.value = true
                }
            }

            wtUsers = users
            wtOldUsers = oldUsers
            val allUsers = users?.toMutableList()
            if (oldUsers != null)
                allUsers!!.addAll(oldUsers)
            wtAllUsers.value = allUsers!!
        }
    }

    private fun isUserInRoom(userId: String, wtUsers: MutableList<WTUser>): Boolean {
        for (wtUser in wtUsers) {
            if (wtUser.userId == userId)
                return true
        }
        return false
    }

    fun observeMessages(roomId: String) {
        if (wtRoom.value != null) {
            FirebaseManager.shared.observeMessages(roomId) { messages, error ->
                wtMessages.value = messages
            }
        }
    }

    fun addMessageToRoom(text: String) {
        createMessage(text)?.let { wtMessage ->
            FirebaseManager.shared.addMessageToRoom(
                wtRoom.value!!.roomId,
                wtMessage
            ) { success: Boolean, error: String? ->
                if (success)
                    Log.e("addMessageToRoom", "success")
                else
                    Log.e("addMessageToRoom", error!!)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createMessage(text: String): WTMessage? {
        WTSessionManager.shared.user?.let { wtUser ->
            return WTMessage(
                messageId = UUID.randomUUID().toString(),
                text = text,
                ownerId = wtUser.userId,
                sendTime = WTUtils.shared.dayTimeStamp()
            )
        }
        return null
    }
}