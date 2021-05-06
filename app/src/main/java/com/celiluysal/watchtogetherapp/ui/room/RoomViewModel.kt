package com.celiluysal.watchtogetherapp.ui.room

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.celiluysal.watchtogetherapp.network.Firebase.FirebaseManager
import com.celiluysal.watchtogetherapp.models.WTMessage
import com.celiluysal.watchtogetherapp.models.WTRoom
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.models.WTVideo
import com.celiluysal.watchtogetherapp.network.Youtube.ApiClient
import com.celiluysal.watchtogetherapp.network.Youtube.models.VideoDetail
import com.celiluysal.watchtogetherapp.utils.WTSessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RoomViewModel : ViewModel() {

    val wtRoom = MutableLiveData<WTRoom>()
    val wtUser = MutableLiveData<WTUser>()
    val wtAllUsers = MutableLiveData<MutableList<WTUser>>()
    val wtMessages = MutableLiveData<MutableList<WTMessage>?>()

    var wtUsers: MutableList<WTUser>? = null
    var wtOldUsers: MutableList<WTUser>? = null

    val didLeaveRoom = MutableLiveData<Boolean>()
    val didKickRoom = MutableLiveData<Boolean>()
    val didRoomDelete = MutableLiveData<Boolean>()

    val errorMessage = MutableLiveData<String>()
    val loadError = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading = MutableLiveData<Boolean>().apply { postValue(false) }


    private fun fetchVideoDetail(videoId: String = "7fYi_tYZhnY") {
        ApiClient.getClient.getVideoInfo(id = videoId)
            .enqueue(object : Callback<VideoDetail> {
                override fun onResponse(call: Call<VideoDetail>, response: Response<VideoDetail>) {
                    Log.e("getData", "onResponse")
                    val videoDetail = response.body()!!

                    videoDetail.items[0].snippet.run {
                        val wtVideo = WTVideo(
                            videoId = videoId,
                            title = title,
                            thumbnail = thumbnails.default.url,
                            channel = channelTitle,
                            duration = 0,
                        )
                    }
                }

                override fun onFailure(call: Call<VideoDetail>, t: Throwable) {
                    Log.e("getData", "onFailure")
                }
            })
    }

    fun userIsOwner(): Boolean = wtRoom.value!!.ownerId == wtUser.value!!.userId

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
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z")
        val currentDateAndTime: String = simpleDateFormat.format(Date())
        WTSessionManager.shared.user?.let { wtUser ->
            return WTMessage(
                messageId = UUID.randomUUID().toString(),
                text = text,
                ownerId = wtUser.userId,
                sendTime = currentDateAndTime
            )
        }
        return null
    }
}