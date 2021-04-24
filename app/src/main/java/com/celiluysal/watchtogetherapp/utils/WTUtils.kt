package com.celiluysal.watchtogetherapp.utils

import android.content.Context
import android.util.Log

class WTUtils {
    companion object{
        val shared = WTUtils()
    }

    fun getAvatarResId(context: Context, avatarId: Int): Int?{
        Log.e("WTUtils", "getAvatarResId")
        context.resources?.getIdentifier(
            "ic_avatar_$avatarId", "drawable", context.packageName
        )?.let {
            return it
        }
        return null
    }
}