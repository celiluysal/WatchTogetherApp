package com.celiluysal.watchtogetherapp.Firebase

import android.util.Log
import com.celiluysal.watchtogetherapp.models.WTMessage
import com.celiluysal.watchtogetherapp.models.WTRoom
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.models.WTVideo
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
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
        dbRef.child("Rooms").child(roomId).get()
            .addOnSuccessListener { snapshot ->
                val room = WTFirebaseUtils.shared.snapshotToRoom(snapshot)
                if (room != null) {
                    if (password != null && password != room.password)
                        Result.invoke(null, "Wrong password")
                    else {
                        addUserToRoom(room.roomId, wtUser) { success: Boolean, error: String? ->
                            if (success)
                                Result.invoke(room, null)
                            else
                                Result.invoke(null, error)
                        }
                    }
                } else
                    Result.invoke(null, "Room parse error")
            }
            .addOnFailureListener {
                Result.invoke(null, it.localizedMessage)
            }
    }

    fun fetchRooms(
        Result: ((wtRooms: MutableList<WTRoom>?, error: String?) -> Unit)
    ) {
        dbRef.child("Rooms").get()
            .addOnSuccessListener { snapshot ->
                val rooms = mutableListOf<WTRoom>()
                for (roomSnapshot in snapshot.children.iterator()) {
                    val room = WTFirebaseUtils.shared.snapshotToRoom(roomSnapshot)
                    if (room != null)
                        rooms.add(room)
                }
                Result.invoke(rooms, null)
            }
            .addOnFailureListener {
                Result.invoke(null, it.localizedMessage)
            }
    }

    fun observeRoomsChild(
        Result: ((success: Boolean, error: String?) -> Unit)
    ) {
        dbRef.child("Rooms").addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Result.invoke(true, null)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.e("observeRoomsChild", "onChildRemoved")
                Result.invoke(true, null)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchRoom(
        roomId: String,
        Result: ((wtRoom: WTRoom?, error: String?) -> Unit)
    ) {
        dbRef.child("Rooms").child(roomId).get()
            .addOnSuccessListener { snapshot ->
                val room = WTFirebaseUtils.shared.snapshotToRoom(snapshot)
                if (room != null)
                    Result.invoke(room, null)
                else
                    Result.invoke(null, "wtRoom is null")
                Log.e("fetchRoom", "on data change")
            }
            .addOnFailureListener {
                Result.invoke(null, it.localizedMessage)
            }
    }

    fun observeMessages(
        roomId: String,
        Result: (messages: MutableList<WTMessage>?, error: String?) -> Unit
    ) {
        dbRef.child("Rooms").child(roomId).child("Messages").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = WTFirebaseUtils.shared.snapshotToMessages(snapshot)
                Result.invoke(messages, null)
            }

            override fun onCancelled(error: DatabaseError) {
                Result.invoke(null, error.details)
            }
        })
    }

    fun observeRoomUsers(
        roomId: String,
        Result: (oldUsers: MutableList<WTUser>?, users: MutableList<WTUser>?, error: String?) -> Unit
    ) {
        val roomRef = dbRef.child("Rooms").child(roomId)
        roomRef.child("Users").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userIds = WTFirebaseUtils.shared.snapshotToUsers(snapshot)
                roomRef.child("OldUsers").get()
                    .addOnSuccessListener { oldUserIdsSnapshot ->
                        val oldUserIds = WTFirebaseUtils.shared.snapshotToOldUsers(oldUserIdsSnapshot)

                        fetchUsers(userIds) { wtUsers, error ->
                            if (oldUserIds != null) {
                                fetchUsers(oldUserIds) { wtOldUsers, error ->
                                    Result.invoke(wtOldUsers, wtUsers, error)
                                }
                            } else {
                                Result.invoke(null, wtUsers, error)
                            }
                        }

                    }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    fun roomsRemoveObserver(
        Result: ((wtRoom: WTRoom, error: String?) -> Unit)
    ) {
        dbRef.child("Rooms").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val room = WTFirebaseUtils.shared.snapshotToRoom(snapshot)
                Log.e("roomsRemoveObserver", "room name: " + room?.roomName)
                room?.let {
                    Result.invoke(it, null)
                }
            }
        })
    }

    fun deleteRoom(
        wtRoom: WTRoom,
        wtUser: WTUser,
        Result: ((success: Boolean, error: String?) -> Unit)
    ) {
        dbRef.child("Rooms").child(wtRoom.roomId).removeValue()
            .addOnSuccessListener {
                Result.invoke(true, null)
            }
            .addOnFailureListener {
                Result.invoke(false, it.localizedMessage)
            }
    }

    fun leaveFromRoom(
        wtRoom: WTRoom,
        wtUser: WTUser,
        Result: ((success: Boolean, error: String?) -> Unit)
    ) {
        addOldUserToRoom(wtRoom.roomId, wtUser, Result)
    }

    private fun addOldUserToRoom(
        roomId: String,
        wtUser: WTUser,
        Result: ((success: Boolean, error: String?) -> Unit)
    ) {
        dbRef.child("Rooms").child(roomId).child("OldUsers").child(wtUser.userId)
            .setValue(
                wtUser.userId
            ).addOnSuccessListener {
                removeUserFromRoom(roomId, wtUser, Result)
            }
            .addOnFailureListener {
                Result.invoke(false, it.localizedMessage)
            }
    }

    private fun removeUserFromRoom(
        roomId: String,
        wtUser: WTUser,
        Result: ((success: Boolean, error: String?) -> Unit)
    ) {
        dbRef.child("Rooms").child(roomId).child("Users").child(wtUser.userId).removeValue()
            .addOnSuccessListener {
                Result.invoke(true, null)
            }
            .addOnFailureListener {
                Result.invoke(false, it.localizedMessage)
            }
    }

    private fun removeOldUserFromRoom(
        roomId: String,
        wtUser: WTUser,
        Result: ((success: Boolean, error: String?) -> Unit)
    ) {
        dbRef.child("Rooms").child(roomId).child("OldUsers").child(wtUser.userId).removeValue()
            .addOnSuccessListener {
                Result.invoke(true, null)
            }
            .addOnFailureListener {
                Result.invoke(false, it.localizedMessage)
            }
    }

    private fun addUserToRoom(
        roomId: String,
        wtUser: WTUser,
        Result: ((success: Boolean, error: String?) -> Unit)
    ) {
        dbRef.child("Rooms").child(roomId).child("OldUsers").child(wtUser.userId).get()
            .addOnSuccessListener {
                removeOldUserFromRoom(roomId, wtUser) { success, error ->
                    if (!success)
                        Result.invoke(success, error)
                }
            }

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

    fun fetchUsers(
        userIds: MutableList<String>,
        Result: (wtUsers: MutableList<WTUser>?, error: String?) -> Unit
    ) {
        dbRef.child("Users").get()
            .addOnSuccessListener { snapshot ->
                val users = mutableListOf<WTUser>()
                for (userId in userIds) {
                    val user = WTFirebaseUtils.shared.snapshotToUser(snapshot.child(userId))
                    if (user != null)
                        users.add(user)
                }
                Result.invoke(users, null)
            }
            .addOnFailureListener {
                Result.invoke(null, it.localizedMessage)
            }
    }

    fun fetchUser(
        userId: String,
        Result: (user: WTUser?, error: String?) -> Unit
    ) {
        dbRef.child("Users").child(userId).get()
            .addOnSuccessListener { dataSnapshot ->
                val dict = dataSnapshot.value as HashMap<*, *>
                val user = WTUser(
                    userId = userId,
                    avatarId = dict["avatarId"].toString().toInt(),
                    fullName = dict["fullName"] as String,
                    email = dict["email"] as String,
                )
                Result.invoke(user, null)
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
                    fetchUser(it.uid, Result)
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