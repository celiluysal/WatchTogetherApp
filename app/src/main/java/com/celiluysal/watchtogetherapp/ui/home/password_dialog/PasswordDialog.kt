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
import com.celiluysal.watchtogetherapp.ui.avatar_picker_dialog.AvatarPickerDialog

class PasswordDialog(private val wtRoom: WTRoom,
                     var clickListener: PasswordDialog.onJoinButtonClickListener
): DialogFragment() {
    interface onJoinButtonClickListener {
        fun onJoinButtonClick(wtRoom: WTRoom?)
    }

    private lateinit var binding: DialogPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        binding = DialogPasswordBinding.inflate(layoutInflater)

        binding.buttonJoin.setOnClickListener {

            if (wtRoom.password == binding.textInputEditTextPassword.text.toString()){
                clickListener.onJoinButtonClick(wtRoom)
            } else {
                Toast.makeText(context,"Şifre yanlış", Toast.LENGTH_SHORT).show()
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
        val height = (resources.displayMetrics.heightPixels * 0.30).toInt()
        dialog!!.window?.setLayout(width, height)
    }


}