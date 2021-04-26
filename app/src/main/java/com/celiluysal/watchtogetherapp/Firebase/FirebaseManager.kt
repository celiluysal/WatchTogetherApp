package com.celiluysal.watchtogetherapp.Firebase

import com.celiluysal.watchtogetherapp.model.WTMessage
import com.celiluysal.watchtogetherapp.model.WTRoom
import com.celiluysal.watchtogetherapp.model.WTUser
import com.celiluysal.watchtogetherapp.model.WTVideo
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.HashMap

class FirebaseManager {
    companion object {
        val shared = FirebaseManager()
    }

    private var dbRef = Firebase.database.reference
    private var auth = Firebase.auth





    fun createRoom(
        roomName: String,
        password: String?,
        owner: WTUser,
        Result: ((wtRoom: WTRoom?, error: String?) -> Unit)
    ) {
        val roomId = UUID.randomUUID().toString()
        dbRef.child("Rooms").child(roomId)
            .setValue(
                hashMapOf<String, Any?>(
                    "roomId" to roomId,
                    "roomName" to roomName,
                    "password" to password,
                    "ownerId" to owner.userId
                )
            )
            .addOnSuccessListener {
                joinRoom(roomId, password, owner, Result)
            }
            .addOnFailureListener {
                Result.invoke(null, it.localizedMessage)
            }


    }

    fun joinRoom(
        roomId: String,
        password: String?,
        wtUser: WTUser,
        Result: ((wtRoom: WTRoom?, error: String?) -> Unit)
    ) {
        dbRef.child("Rooms").child(roomId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val room = WTFirebaseUtils.shared.snapshotToRoom(snapshot)
                    if (room == null) {
                        Result.invoke(null, "Room parse error")
                        return
                    }

                    if (password != null && password != room.password)
                        Result.invoke(null, "Wrong password")
                    else {
                        addUserToRoom(room.roomId, wtUser){ success: Boolean, error: String? ->
                            if (success)
                                Result.invoke(room, null)
                            else
                                Result.invoke(null, error)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Result.invoke(null, error.details)
                }

            })


    }

    fun fetchRoom(dataSnapshot: DataSnapshot) {

    }


    private fun addUserToRoom(
        roomId: String,
        wtUser: WTUser,
        Result: ((success: Boolean, error: String?) -> Unit)
    ) {
        dbRef.child("Rooms").child(roomId).child("Users").child(wtUser.userId)
            .setValue(
                wtUser.userId
            ).addOnSuccessListener {
                Result.invoke(true, null)
            }
            .addOnFailureListener {
                Result.invoke(false, it.localizedMessage)
            }
    }


    fun addVideoToRoomPlaylist(
        roomId: String,
        video: WTVideo,
        Result: ((success: Boolean, error: String?) -> Unit)
    ) {
        dbRef.child("Rooms").child(roomId).child("Playlist").push()
            .setValue(
                video.toDict()
            )
            .addOnSuccessListener {
                Result.invoke(true, null)
            }
            .addOnFailureListener {
                Result.invoke(false, it.localizedMessage)
            }
    }


    fun addMessageToRoom(
        roomId: String,
        message: WTMessage,
        Result: ((success: Boolean, error: String?) -> Unit)
    ) {
        dbRef.child("Rooms").child(roomId).child("Messages").push()
            .setValue(
                message.toDict()
            )
            .addOnSuccessListener {
                Result.invoke(true, null)
            }
            .addOnFailureListener {
                Result.invoke(false, it.localizedMessage)
            }

    }


    private fun saveUserInfo(
        request: RegisterRequestModel,
        uid: String,
        Result: ((success: Boolean, error: String?) -> Unit)
    ) {
        dbRef.child("Users").child(uid).setValue(
            hashMapOf<String, Any?>(
                "userId" to uid,
                "avatarId" to request.avatarId,
                "fullName" to request.fullName,
                "email" to request.email
            )
        )
            .addOnSuccessListener {
                Result.invoke(true, "")
            }
            .addOnFailureListener {
                Result.invoke(false, it.localizedMessage)
            }

    }

    fun register(
        request: RegisterRequestModel,
        Result: (user: WTUser?, error: String?) -> Unit
    ) {
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

    fun fetchUserInfo(
        uid: String,
        Result: (user: WTUser?, error: String?) -> Unit
    ) {
        dbRef.child("Users").child(uid).get()
            .addOnSuccessListener { dataSnapshot ->
                val dict = dataSnapshot.value as HashMap<*, *>
                val user = WTUser(
                    userId = uid,
                    avatarId = dict["avatarId"].toString().toInt(),
                    fullName = dict["fullName"] as String,
                    email = dict["email"] as String,
                )
                Result.invoke(user, "")
            }
            .addOnFailureListener {
                Result.invoke(null, it.localizedMessage)
            }
    }

    fun login(
        email: String,
        password: String,
        Result: (user: WTUser?, error: String?) -> Unit
    ) {
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

    fun updateAvatar(
        wtUser: WTUser,
        id: Int,
        Result: (user: WTUser?, error: String?) -> Unit
    ) {
        wtUser.userId?.let { uid ->
            dbRef.child("Users").child(uid).child("avatarId").setValue(id)
                .addOnSuccessListener {
                    wtUser.avatarId = id
                    Result.invoke(wtUser, null)
                }
                .addOnFailureListener {
                    Result.invoke(null, it.localizedMessage)
                }
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun signOut() = auth.signOut()


}