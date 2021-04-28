package com.celiluysal.watchtogetherapp.utils

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.models.WTUser
import com.celiluysal.watchtogetherapp.ui.dialog.AvatarImage

class WTUtils {
    companion object {
        val shared = WTUtils()
    }

    val avatarCount = 20

    fun getAvatarResId(context: Context?, avatarId: Int): Int {
        context?.resources?.getIdentifier(
            "ic_avatar_$avatarId", "drawable", "com.celiluysal.watchtogetherapp"
        )?.let {
            return it
        }
        return R.drawable.ic_avatar_1
    }

    fun getAvatarList(context: Context?): ArrayList<AvatarImage> {
        val avatarList: MutableList<AvatarImage> = mutableListOf()
        for (i in 1..avatarCount) {
            context?.let { _context ->
                getAvatarResId(_context, i).let { redId ->
                    avatarList.add(AvatarImage(i, redId))
                }
            }
        }
        return avatarList as ArrayList<AvatarImage>
    }
}