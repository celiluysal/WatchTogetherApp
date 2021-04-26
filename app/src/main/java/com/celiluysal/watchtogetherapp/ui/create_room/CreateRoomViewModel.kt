package com.celiluysal.watchtogetherapp.ui.create_room

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.celiluysal.watchtogetherapp.Firebase.FirebaseManager
import com.celiluysal.watchtogetherapp.model.WTRoom
import com.celiluysal.watchtogetherapp.utils.WTSessionManager

class CreateRoomViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val loadError = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading = MutableLiveData<Boolean>().apply { postValue(false) }


    fun createRoom(roomName: String, password: String?) {
        WTSessionManager.shared.user?.let { wtUser ->
            FirebaseManager.shared.createRoom(
                roomName,
                password,
                wtUser
            ) { wtRoom: WTRoom?, error: String? ->
                if (wtRoom != null) {
                    Log.e("CreateRoomViewModel", "createRoom")
                } else
                    Log.e("CreateRoomViewModel", error!!)

            }
        }

    }


    // TODO: Implement the ViewModel
}