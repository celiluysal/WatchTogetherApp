package com.celiluysal.watchtogetherapp.Firebase

import com.celiluysal.watchtogetherapp.model.WTContent
import com.celiluysal.watchtogetherapp.model.WTMessage
import com.celiluysal.watchtogetherapp.model.WTRoom
import com.celiluysal.watchtogetherapp.model.WTVideo
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
            messages = snapshotToMessages(roomSnapshot.child("Messages"))
        )
    }

    fun snapshotToUsers(usersSnapshot: DataSnapshot): MutableList<String> {
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
            currentTime = contentDict["currentTime"] as Int,
            isPlaying = contentDict["isPlaying"] as Boolean,
            video = WTVideo(
                videoId = videoDict["videoId"] as String,
                title = videoDict["title"] as String,
                thumbnail = videoDict["thumbnail"] as String,
                channel = videoDict["channel"] as String,
                duration = videoDict["duration"] as Int
            )
        )
    }

    fun snapshotToPlaylist(playlistSnapshot: DataSnapshot): MutableList<WTVideo>? {
        val playlist = mutableListOf<WTVideo>()
        for (child in playlistSnapshot.children.iterator()) {
            val videoDict = child.value as HashMap<*, *>
            playlist.add(
                WTVideo(
                    videoId = videoDict["videoId"] as String,
                    title = videoDict["title"] as String,
                    thumbnail = videoDict["thumbnail"] as String,
                    channel = videoDict["channel"] as String,
                    duration = videoDict["duration"] as Int
                )
            )
        }
        return playlist
    }

    fun snapshotToMessages(messagesSnapshot: DataSnapshot): MutableList<WTMessage> {
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