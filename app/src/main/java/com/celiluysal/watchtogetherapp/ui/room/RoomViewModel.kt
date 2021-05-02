package com.celiluysal.watchtogetherapp.ui.room

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.celiluysal.watchtogetherapp.Firebase.FirebaseManager
import com.celiluysal.watchtogetherapp.models.WTMessage
import com.celiluysal.watchtogetherapp.models.WTRoom
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.utils.WTSessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

class RoomViewModel : ViewModel() {

    val wtRoom = MutableLiveData<WTRoom>()
    val wtUser = MutableLiveData<WTUser>()
    val wtUsers = MutableLiveData<MutableList<WTUser>>()
    val wtMessages = MutableLiveData<MutableList<WTMessage>?>()
//    val wtOldUsers = MutableLiveData<MutableList<WTUser>?>()

    val chatModelDidComplete = MutableLiveData<Boolean>()

    val didLeaveRoom = MutableLiveData<Boolean>()
    val didRoomDelete = MutableLiveData<Boolean>()

    val errorMessage = MutableLiveData<String>()
    val loadError = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading = MutableLiveData<Boolean>().apply { postValue(false) }

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
        FirebaseManager.shared.observeRoomUsers(roomId) { users, error ->
            wtUsers.value = users
        }
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