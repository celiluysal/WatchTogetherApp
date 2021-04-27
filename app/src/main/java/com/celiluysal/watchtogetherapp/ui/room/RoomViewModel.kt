package com.celiluysal.watchtogetherapp.ui.room

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.celiluysal.watchtogetherapp.Firebase.FirebaseManager
import com.celiluysal.watchtogetherapp.models.WTMessage
import com.celiluysal.watchtogetherapp.models.WTRoom
import com.celiluysal.watchtogetherapp.utils.WTSessionManager
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

class RoomViewModel : ViewModel() {

    val wtRoom = MutableLiveData<WTRoom>()

    val errorMessage = MutableLiveData<String>()
    val loadError = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading = MutableLiveData<Boolean>().apply { postValue(false) }

    fun fetchRoom(roomId: String){
        FirebaseManager.shared.fetchRoom(roomId) { wtRoom: WTRoom?, error: String? ->
            if (wtRoom != null) {
                Log.e("fetchRoom", "success")
                this.wtRoom.value = wtRoom
            }
            else
                Log.e("fetchRoom", error!!)

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

    fun createMessage(text: String): WTMessage? {
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