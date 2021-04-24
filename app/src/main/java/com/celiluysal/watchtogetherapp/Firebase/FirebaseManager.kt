package com.celiluysal.watchtogetherapp.Firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.selects.whileSelect
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class FirebaseManager {
    companion object {
        val shared = FirebaseManager()
    }

    private var dbRef = Firebase.database.reference
    private var auth = Firebase.auth

    private fun saveUserInfo(request: RegisterRequestModel, uid: String, Result: ((success: Boolean, error: String?) -> Unit)) {
        val dict = hashMapOf<String, Any?>()
        dict["userId"] = uid
        dict["avatarId"] = request.avatarId
        dict["fullName"] = request.fullName
        dict["email"] = request.email

        dbRef.child("Users").child(uid).setValue(dict)
                .addOnSuccessListener {
                    Result.invoke(true, "")
                }
                .addOnFailureListener {
                    Result.invoke(false, it.localizedMessage)
                }

    }

    fun register(request: RegisterRequestModel, Result: (user: WTUser?, error: String?) -> Unit) {
        auth.createUserWithEmailAndPassword(request.email, request.password)
                .addOnSuccessListener {
                    auth.currentUser?.let { firebaseUser ->
                        saveUserInfo(request, firebaseUser.uid) { success, error ->
                            if (success) {
                                val user = WTUser(
                                        userId = firebaseUser.uid,
                                        avatarId = request.avatarId,
                                        fullName = request.fullName,
                                        email = request.email,
                                )
                                Result.invoke(user, "")
                            } else Result.invoke(null, error)
                        }
                    }
                }
                .addOnFailureListener {
                    Result.invoke(null, it.localizedMessage)
                }
    }


    fun fetchUserInfo(uid: String, Result: (user: WTUser?, error: String?) -> Unit) {
        dbRef.child("Users").child(uid).get()
                .addOnSuccessListener { dataSnapshot ->
                    val dict = dataSnapshot.value as HashMap<*, *>
                    val user = WTUser(
                            userId = uid,
                            avatarId = dict["avatarId"].toString().toInt(),
                            fullName = dict["fullName"] as String,
                            email = dict["email"] as String,
                    )
                    Result.invoke(user,"")
                }
                .addOnFailureListener {
                    Result.invoke(null, it.localizedMessage)
                }
    }

    fun login(email: String, password: String, Result: (user: WTUser?, error: String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.let {
                        fetchUserInfo(it.uid, Result)
                    }
                }
                .addOnFailureListener {
                    Result.invoke(null, it.localizedMessage)
                }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun signOut() = auth.signOut()


}