package com.celiluysal.watchtogetherapp.Firebase

import com.celiluysal.watchtogetherapp.models.*
import com.google.firebase.database.DataSnapshot

class WTFirebaseUtils {

    companion object {
        val shared = WTFirebaseUtils()
    }

    fun snapshotToRoom(roomSnapshot: DataSnapshot): WTRoom? {
        val roomDict = if (roomSnapshot.value != null)
            roomSnapshot.value as HashMap<*, *> else return null

        return WTRoom(
            roomId = roomDict["roomId"] as String,
            password = roomDict["password"] as String?,
            roomName = roomDict["roomName"] as String,
            ownerId = roomDict["ownerId"] as String,
            content = snapshotToContent(roomSnapshot.child("Content")),
            playlist = snapshotToPlaylist(roomSnapshot.child("Playlist")),
            users = snapshotToUsers(roomSnapshot.child("Users")),
            oldUsers = snapshotToOldUsers(roomSnapshot.child("OldUsers")),
            messages = snapshotToMessages(roomSnapshot.child("Messages"))
        )
    }

    fun snapshotToUser(usersSnapshot: DataSnapshot): WTUser? {
        val userDict = if (usersSnapshot.value != null)
            usersSnapshot.value as HashMap<*,*> else return null

        return WTUser(
            userId = userDict["userId"] as String,
            avatarId = userDict["avatarId"].toString().toInt(),
            fullName = userDict["fullName"] as String,
            email = userDict["email"] as String
        )
    }

    fun snapshotToRooms(roomsSnapshot: DataSnapshot): MutableList<WTRoom> {
        val rooms = mutableListOf<WTRoom>()
        for (child in roomsSnapshot.children.iterator()) {
            val roomSnapshot = child.value as WTRoom
            val room = snapshotToRoom(roomsSnapshot)
            room?.let { rooms.add(room) }
        }
        return rooms
    }

    fun snapshotToUsers(usersSnapshot: DataSnapshot): MutableList<String> {
        val users = mutableListOf<String>()
        for (child in usersSnapshot.children.iterator()) {
            val userId = child.value as String
            users.add(userId)
        }
        return users
    }

    fun snapshotToOldUsers(usersSnapshot: DataSnapshot): MutableList<String>? {
        if (usersSnapshot.value == null)
            return null

        val users = mutableListOf<String>()
        for (child in usersSnapshot.children.iterator()) {
            val userId = child.value as String
            users.add(userId)
        }
        return users
    }

    fun snapshotToContent(contentSnapshot: DataSnapshot): WTContent? {
        val contentDict = if (contentSnapshot.value != null)
            contentSnapshot.value as HashMap<*, *> else return null

        val videoDict = contentSnapshot.child("Video").value as HashMap<*, *>
        return WTContent(
            currentTime = contentDict["currentTime"].toString().toInt(),
            isPlaying = contentDict["isPlaying"] as Boolean,
            video = WTVideo(
                videoId = videoDict["videoId"] as String,
                title = videoDict["title"] as String,
                thumbnail = videoDict["thumbnail"] as String,
                channel = videoDict["channel"] as String,
                duration = videoDict["duration"].toString().toInt()
            )
        )
    }

    fun snapshotToPlaylist(playlistSnapshot: DataSnapshot): MutableList<WTVideo>? {
        if (playlistSnapshot.value == null)
            return null
        val playlist = mutableListOf<WTVideo>()
        for (child in playlistSnapshot.children.iterator()) {
            val videoDict = child.value as HashMap<*, *>
            playlist.add(
                WTVideo(
                    videoId = videoDict["videoId"] as String,
                    title = videoDict["title"] as String,
                    thumbnail = videoDict["thumbnail"] as String,
                    channel = videoDict["channel"] as String,
                    duration = (videoDict["duration"]).toString().toInt()
                )
            )
        }
        return playlist
    }

    fun snapshotToMessages(messagesSnapshot: DataSnapshot): MutableList<WTMessage>? {
        if (messagesSnapshot.value == null)
            return null
        val messages = mutableListOf<WTMessage>()
        for (child in messagesSnapshot.children.iterator()) {
            val messageDict = child.value as HashMap<*, *>
            messages.add(
                WTMessage(
                    messageId = messageDict["messageId"] as String,
                    text = messageDict["text"] as String,
                    ownerId = messageDict["ownerId"] as String,
                    sendTime = messageDict["sendTime"] as String,
                )
            )
        }
        return messages
    }


}