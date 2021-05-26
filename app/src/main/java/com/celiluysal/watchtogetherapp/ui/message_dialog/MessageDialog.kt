package com.celiluysal.watchtogetherapp.ui.message_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.databinding.DialogMessageBinding

class MessageDialog(val messageText: String, var clickListener: OnMessageDialogClickListener): DialogFragment() {
    private lateinit var binding: DialogMessageBinding

    var leftButtonText: String? = null
    var rightButtonText: String? = null

    var leftButtonColor: Int? = null
    var rightButtonColor: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        binding = DialogMessageBinding.inflate(layoutInflater)

        binding.textViewDialogMessage.text = messageText

        binding.buttonLeft.visibility = Button.GONE
        binding.buttonRight.visibility = Button.GONE

        leftButtonText?.let { text ->
            binding.buttonLeft.visibility = Button.VISIBLE
            binding.buttonLeft.text = text
            binding.buttonLeft.setOnClickListener { clickListener.onLeftButtonClick() }
        }

        rightButtonText?.let { text ->
            binding.buttonRight.visibility = Button.VISIBLE
            binding.buttonRight.text = text
            binding.buttonRight.setOnClickListener { clickListener.onRightButtonClick() }
        }

        leftButtonColor?.let { binding.buttonLeft.setBackgroundColor(it) }
        rightButtonColor?.let { binding.buttonRight.setBackgroundColor(it) }

        return binding.root
    }

    interface OnMessageDialogClickListener {
        fun onLeftButtonClick() {}
        fun onRightButtonClick() {}
    }


    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window?.setLayout(width, height)
    }
}