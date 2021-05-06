package com.celiluysal.watchtogetherapp.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.celiluysal.watchtogetherapp.network.Firebase.FirebaseManager
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.utils.WTSessionManager

class LoginViewModel : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    val loadError = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading = MutableLiveData<Boolean>().apply { postValue(false) }

    val loggedIn = MutableLiveData<Boolean>().apply { postValue(false) }

    fun login(email: String, password: String) {
        loading.value = true
        FirebaseManager.shared.login(email, password) { wtUser: WTUser?, error: String? ->
            if (wtUser != null) {
                WTSessionManager.shared.loggedIn(wtUser)
                loadError.value = false
                loading.value = false
                loggedIn.value = true
            } else {
                errorMessage.value = error
                loadError.value = true
                loading.value = false
            }
        }
    }

}