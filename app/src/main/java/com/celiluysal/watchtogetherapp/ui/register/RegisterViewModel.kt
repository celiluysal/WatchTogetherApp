package com.celiluysal.watchtogetherapp.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.celiluysal.watchtogetherapp.Firebase.FirebaseManager
import com.celiluysal.watchtogetherapp.Firebase.RegisterRequestModel
import com.celiluysal.watchtogetherapp.Firebase.WTUser
import com.celiluysal.watchtogetherapp.utils.WTSessionManager

class RegisterViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val loadError = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading = MutableLiveData<Boolean>().apply { postValue(false) }

    fun register(request: RegisterRequestModel) {
        loading.value = true
        FirebaseManager.shared.register(request) { wtUser: WTUser?, error: String? ->
            if (wtUser != null) {
                login(request.email, request.password)
                loadError.value = false
                loading.value = false
            } else {
                errorMessage.value = error
                loadError.value = true
                loading.value = false
            }

        }
    }

    private fun login(email: String, password: String) {
        FirebaseManager.shared.login(email, password) { wtUser: WTUser?, error: String? ->
            if (wtUser != null)
                WTSessionManager.shared.loggedIn(wtUser)
            else
                errorMessage.value = error
        }
    }


}