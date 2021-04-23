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
        if (wtUser.value != null)
            wtUser.value.apply {  }
        else
            fetchUser()
    }

    fun fetchUser() {

        Log.e("ProfileViewModel","fetchUser")
        loading.value = true
        WTSessionManager.shared.fetchUser { success: Boolean, error: String? ->
            Log.e("ProfileViewModel","fetchUser1")
            if (success) {
                wtUser.value = WTSessionManager.shared.user
                loading.value = false
                Log.e("ProfileViewModel","success")
            } else {
                loading.value = false
                loadError.value = true
                errorMessage.value = error
                Log.e("ProfileViewModel", error!!)
            }
        }
    }
}