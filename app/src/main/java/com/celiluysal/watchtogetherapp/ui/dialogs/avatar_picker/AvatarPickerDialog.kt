package com.celiluysal.watchtogetherapp.ui.dialogs.avatar_picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.celiluysal.watchtogetherapp.databinding.DialogAvatarPickerBinding
import com.celiluysal.watchtogetherapp.models.AvatarImage
import com.celiluysal.watchtogetherapp.utils.WTUtils

class AvatarPickerDialog(var defaultAvatarId: Int,
                         var clickListener: onUpdateButtonClickListener
) :
    DialogFragment(), AvatarRecyclerViewAdapter.onAvatarItemClickListener {
    private lateinit var binding: DialogAvatarPickerBinding
    private lateinit var avatarRecyclerViewAdapter: AvatarRecyclerViewAdapter
    private lateinit var selectedItem: AvatarImage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogAvatarPickerBinding.inflate(layoutInflater)

        binding.imageViewClose.setOnClickListener {
            dialog?.dismiss()
        }

        fill()

        return binding.root
    }

    private fun fill() {

        val list = WTUtils.shared.getAvatarList(activity?.baseContext)

        selectedItem = AvatarImage(defaultAvatarId, WTUtils.shared.getAvatarResId(context,defaultAvatarId))
        binding.imageViewAvatar.setImageResource(selectedItem.resource)

        binding.recyclerViewAvatar.layoutManager = GridLayoutManager(activity, 4)
        avatarRecyclerViewAdapter = AvatarRecyclerViewAdapter(list, this)
        binding.recyclerViewAvatar.adapter = avatarRecyclerViewAdapter

        binding.buttonUpdate.setOnClickListener {
            clickListener.onUpdateButtonClick(selectedItem)
        }


    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.90).toInt()
        dialog!!.window?.setLayout(width, height)
    }

    override fun onAvatarItemClick(item: AvatarImage, position: Int) {
        binding.imageViewAvatar.setImageResource(item.resource)
        selectedItem = item
    }

    interface onUpdateButtonClickListener {
        fun onUpdateButtonClick(item: AvatarImage)
    }
}