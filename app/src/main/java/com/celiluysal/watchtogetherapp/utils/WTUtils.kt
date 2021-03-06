package com.celiluysal.watchtogetherapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.base.Constant
import com.celiluysal.watchtogetherapp.models.AvatarImage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class WTUtils {
    companion object {
        val shared = WTUtils()
    }
    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")

    fun dayTimeStamp(): String = simpleDateFormat.format(Date())

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
        for (i in 1..Constant.AVATAR_COUNT) {
            context?.let { _context ->
                getAvatarResId(_context, i).let { redId ->
                    avatarList.add(AvatarImage(i, redId))
                }
            }
        }
        return avatarList as ArrayList<AvatarImage>
    }

    fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }
}