package com.celiluysal.watchtogetherapp.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.celiluysal.watchtogetherapp.network.Firebase.FirebaseManager
import com.celiluysal.watchtogetherapp.models.WTRoom
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.utils.WTSessionManager

class RoomsViewModel : ViewModel() {
    val wtRooms = MutableLiveData<MutableList<WTRoom>>()
    val wtRoomId = MutableLiveData<String>()

    val errorMessage = MutableLiveData<String>()
    val loadError = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading = MutableLiveData<Boolean>().apply { postValue(false) }
    
    fun observeRoomsChild(){
        FirebaseManager.shared.observeRoomsChild {success, error ->
            if (success) {
                fetchRooms()
            } else
                Log.e("RoomsViewModel", error!!)
        }
    }

    fun fetchRooms() {
        FirebaseManager.shared.fetchRooms { wtRooms, error ->
            if (wtRooms != null) {
                Log.e("RoomsViewModel", "fetchRooms success")
                WTSessionManager.shared.user?.let { wtUser ->
                    val room = isUserInAnyRoom(wtUser, wtRooms)
                    if (room != null)
                        joinRoom(room.roomId, null)
                    else
                        this.wtRooms.value = wtRooms
                }
            } else
                Log.e("RoomsViewModel", error!!)
        }
    }

    fun isUserInAnyRoom(wtUser: WTUser, wtRooms: MutableList<WTRoom>): WTRoom? {
        for (wtRoom in wtRooms) {
            if (wtRoom.users.contains(wtUser.userId))
                if (wtRoom.password == null)
                    return wtRoom
        }
        return null
    }

    fun joinRoom(roomId: String, password: String?) {

        WTSessionManager.shared.user?.let { wtUser ->
            FirebaseManager.shared.joinRoom(
                roomId,
                password,
                wtUser
            ) { wtRoom: WTRoom?, error: String? ->
                if (wtRoom != null)
                    wtRoomId.value = wtRoom.roomId
                else
                    Log.e("RoomsViewModel", error!!)

            }
        }
    }
}