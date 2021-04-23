package com.celiluysal.watchtogetherapp.utils

import android.util.Log
import com.celiluysal.watchtogetherapp.Firebase.FirebaseManager
import com.celiluysal.watchtogetherapp.Firebase.WTUser

class WTSessionManager {
    companion object {
        val shared = WTSessionManager()
    }

    private var firebaseManager = FirebaseManager.shared
    lateinit var user: WTUser

    fun isLoggedIn(): Boolean = firebaseManager.getCurrentUser() != null

    fun loggedIn(user: WTUser) {
        this.user = user
        Log.e("WTSessionManager","loggedIn")
        Log.e("WTSessionManager","loggedIn"+ user.email.toString())

    }

    fun logOut() {
        firebaseManager.signOut()
    }

    fun fetchUser(Result: (success: Boolean, error: String?) -> Unit) {
        ////////
        //FirebaseManager.shared.login("c1@mail.com","123456") { wtUser: WTUser?, s: String? -> }
        ////////

        val currentUser = firebaseManager.getCurrentUser()
        Log.e("WTSessionManager","fetchUser")


        currentUser?.let { firebaseUser ->
            firebaseManager.fetchUserInfo(firebaseUser.uid) { wtUser: WTUser?, error: String? ->
                if (wtUser != null){
                    Log.e("WTSessionManager","fetchUserInfo")

                    loggedIn(wtUser)
                    Result.invoke(true, "")
                } else {
                    Result.invoke(false, error)
                }
            }

        }
    }

}