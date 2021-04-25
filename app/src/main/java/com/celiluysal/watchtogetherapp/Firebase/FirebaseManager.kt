package com.celiluysal.watchtogetherapp.Firebase

import com.celiluysal.watchtogetherapp.model.WTRoom
import com.celiluysal.watchtogetherapp.model.WTUser
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.collections.HashMap

class FirebaseManager {
    companion object {
        val shared = FirebaseManager()
    }

    private var dbRef = Firebase.database.reference
    private var auth = Firebase.auth


//    func joinRoom(user: WTUser, roomId: String, password: String?, completion: @escaping ((Result<Room, PresentableError>) -> Void)) {
//
//        self.dbRef.child("Rooms").child(roomId).observe(.value) { (snapshot) in
//
//                guard let room = self.fetchRoomFrom(snapshot: snapshot) else {
//            completion(.failure(.init(message: "Parse Error")))
//            return
//        }
//
//            if let password = room.password, password != password {
//                completion(.failure(.init(message: "Wrong Password")))
//            } else {
//                self.addUserToRoom(roomId: roomId, user: user) { (result) in
//                        switch result {
//                    case .success:
//                    completion(.success(room))
//                    break
//                    case let .failure(error):
//                    completion(.failure(error))
//                }
//                }
//            }
//
//        }
//
//    }

    fun joinRoom(roomId: String,
                 password: String?,
                 wtUser: WTUser,
                 Result: ((success: Boolean, error: String?) -> Unit)) {
        dbRef.child("Rooms").child(roomId).get()
            .addOnSuccessListener { dataSnapshot ->

            }
            .addOnFailureListener {
                Result.invoke(false, it.localizedMessage)
            }

    }

    fun fetchRoom(dataSnapshot: DataSnapshot) {
        
    }


    fun addUserToRoom(
        roomId: String,
        wtUser: WTUser,
        Result: ((success: Boolean, error: String?) -> Unit)
    ) {
        dbRef.child("Rooms").child(roomId).child("Users").child(wtUser.userId)
            .setValue(
                hashMapOf<String, Any?>(
                    "userId" to wtUser.userId,
                    "avatarId" to wtUser.avatarId,
                    "fullName" to wtUser.fullName,
                    "email" to wtUser.email
                )
            ).addOnSuccessListener {
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
                Result.invoke(user, "")
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

    fun updateAvatar(wtUser: WTUser, id: Int, Result: (user: WTUser?, error: String?) -> Unit) {
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