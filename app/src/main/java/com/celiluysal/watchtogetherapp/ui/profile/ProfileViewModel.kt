package com.celiluysal.watchtogetherapp.ui.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.celiluysal.watchtogetherapp.Firebase.WTUser
import com.celiluysal.watchtogetherapp.utils.WTSessionManager

class ProfileViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val loadError = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading = MutableLiveData<Boolean>().apply { postValue(false) }

    val wtUser = MutableLiveData<WTUser>()

    fun refresh(){
        WTSessionManager.shared.user.let {
            if (it != null)
                wtUser.value = it
            else
                fetchUser()
        }
    }

    fun logout() {
        WTSessionManager.shared.logOut()
    }

    fun updateAvatar(id: Int){
        loading.value = true
        WTSessionManager.shared.updateAvatar(id) { wtUser: WTUser?, error: String? ->
            if (wtUser != null) {
                loading.value = false
                loadError.value = false
                this.wtUser.value = wtUser
                Log.e("ProfileViewModel","updateAvatar success")
            } else {
                loading.value = false
                loadError.value = true
                errorMessage.value = error
                Log.e("ProfileViewModel", error!!)
            }
        }
    }

    private fun fetchUser() {
        loading.value = true
        WTSessionManager.shared.fetchUser { success: Boolean, error: String? ->
            Log.e("ProfileViewModel","fetchUser1")
            if (success) {
                wtUser.value = WTSessionManager.shared.user
                loading.value = false
                Log.e("ProfileViewModel","fetchUser success")
            } else {
                loading.value = false
                loadError.value = true
                errorMessage.value = error
                Log.e("ProfileViewModel", error!!)
            }
        }
    }
}