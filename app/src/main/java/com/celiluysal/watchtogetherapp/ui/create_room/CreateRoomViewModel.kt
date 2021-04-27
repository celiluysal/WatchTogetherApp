package com.celiluysal.watchtogetherapp.ui.create_room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.celiluysal.watchtogetherapp.Firebase.FirebaseManager
import com.celiluysal.watchtogetherapp.models.WTRoom
import com.celiluysal.watchtogetherapp.utils.WTSessionManager

class CreateRoomViewModel : ViewModel() {

    val wtRoom = MutableLiveData<WTRoom>()

    val errorMessage = MutableLiveData<String>()
    val loadError = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading = MutableLiveData<Boolean>().apply { postValue(false) }


    fun createRoom(roomName: String, password: String?) {
        loading.value = true
        WTSessionManager.shared.user?.let { wtUser ->
            FirebaseManager.shared.createRoom(
                roomName,
                password,
                wtUser
            ) { wtRoom: WTRoom?, error: String? ->
                if (wtRoom != null) {
                    loading.value = false
                    loadError.value = false
                    this.wtRoom.value = wtRoom
                } else {
                    loading.value = false
                    loadError.value = true
                    errorMessage.value = error
                }
            }
        }
    }
}