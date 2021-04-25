package com.celiluysal.watchtogetherapp.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.celiluysal.watchtogetherapp.Firebase.FirebaseManager
import com.celiluysal.watchtogetherapp.Firebase.RegisterRequestModel
import com.celiluysal.watchtogetherapp.model.WTUser
import com.celiluysal.watchtogetherapp.utils.WTSessionManager
import com.celiluysal.watchtogetherapp.utils.WTUtils

class RegisterViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val loadError = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading = MutableLiveData<Boolean>().apply { postValue(false) }

    val avatarId = MutableLiveData<Int>().apply { postValue(
        (1..WTUtils.shared.avatarCount).random()
    ) }

    fun register(request: RegisterRequestModel) {
        loading.value = true
        FirebaseManager.shared.register(request) { wtUser: WTUser?, error: String? ->
            if (wtUser != null) {
                login(request.email, request.password)
            } else {
                errorMessage.value = error
                loadError.value = true
                loading.value = false
            }

        }
    }

    private fun login(email: String, password: String) {
        loading.value = true
        FirebaseManager.shared.login(email, password) { wtUser: WTUser?, error: String? ->
            if (wtUser != null) {
                loadError.postValue(false)
                loading.value = false
                WTSessionManager.shared.loggedIn(wtUser)
            } else {
                errorMessage.value = error
                loadError.value = true
                loading.value = false
            }
        }
    }


}