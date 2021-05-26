package com.celiluysal.watchtogetherapp.ui.home.password_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.databinding.DialogPasswordBinding
import com.celiluysal.watchtogetherapp.models.WTRoom
import com.celiluysal.watchtogetherapp.ui.message_dialog.MessageDialog

class PasswordDialog(
    private val wtRoom: WTRoom,
    var clickListener: PasswordDialog.OnJoinButtonClickListener
) : DialogFragment() {
    interface OnJoinButtonClickListener {
        fun onJoinButtonClick(wtRoom: WTRoom?)
    }

    private lateinit var binding: DialogPasswordBinding
    private lateinit var messageDialog: MessageDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        binding = DialogPasswordBinding.inflate(layoutInflater)

        binding.buttonJoin.setOnClickListener {

            if (wtRoom.password == binding.textInputEditTextPassword.text.toString()) {
                clickListener.onJoinButtonClick(wtRoom)
            } else {

                messageDialog = MessageDialog(
                    "Hatalı şifre!",
                    object : MessageDialog.OnMessageDialogClickListener {
                        override fun onLeftButtonClick() {
                            messageDialog.dismiss()
                        }
                    })

                messageDialog.leftButtonText = "Tamam"
                activity?.supportFragmentManager?.let { it1 ->
                    messageDialog.show(
                        it1,
                        "MessageDialog"
                    )
                }

                clickListener.onJoinButtonClick(null)
            }

        }

        binding.imageViewClose.setOnClickListener {
            dialog?.dismiss()
        }

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window?.setLayout(width, height)
    }
}