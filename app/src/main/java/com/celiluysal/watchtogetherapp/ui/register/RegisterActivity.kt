package com.celiluysal.watchtogetherapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.celiluysal.watchtogetherapp.Firebase.RegisterRequestModel
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.base.BaseActivity
import com.celiluysal.watchtogetherapp.databinding.ActivityRegisterBinding
import com.celiluysal.watchtogetherapp.models.AvatarImage
import com.celiluysal.watchtogetherapp.ui.dialogs.avatar_picker.AvatarPickerDialog
import com.celiluysal.watchtogetherapp.ui.login.LoginActivity
import com.celiluysal.watchtogetherapp.ui.main.MainActivity
import com.celiluysal.watchtogetherapp.utils.WTUtils

class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(),
    AvatarPickerDialog.onUpdateButtonClickListener {

    private lateinit var avatarPickerDialog: AvatarPickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)



        viewModel.avatarId.observe(this, { id ->
            binding.avatarContainer.imageViewAvatar.setImageResource(
                WTUtils.shared.getAvatarResId(applicationContext, id)
            )

            binding.avatarContainer.imageViewEditAvatar.setOnClickListener {
                supportFragmentManager.let {
                    avatarPickerDialog =
                        AvatarPickerDialog(id, this)
                    avatarPickerDialog.show(it, "AvatarPickerDialog")
                }
            }
        })



        binding.buttonRegister.setOnClickListener {
            if (checkFields()) {
                viewModel.register(RegisterRequestModel(
                    fullName = binding.textInputEditTextFullName.text.toString(),
                    email = binding.textInputEditTextEmail.text.toString(),
                    password = binding.textInputEditTextPassword.text.toString(),
                    avatarId = viewModel.avatarId.value!!
                ))

                viewModel.loadError.observe(this, {
                    if (!it) {
                        Log.e("RegisterActivity", "login")
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                })
            }
        }


        binding.textViewLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onUpdateButtonClick(item: AvatarImage) {
        viewModel.avatarId.value = item.id
        avatarPickerDialog.dismiss()
    }

    private fun checkFields(): Boolean {
        fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

        when {
            binding.textInputEditTextFullName.text.toString().isNullOrBlank() -> {
                toast(getString(R.string.full_name) + " " + getString(R.string.field_cant_be_empty))
                return false
            }
            else -> {
                when {
                    binding.textInputEditTextEmail.text.toString().isNullOrBlank() -> {
                        toast(getString(R.string.email) + " " + getString(R.string.field_cant_be_empty))
                        return false
                    }
                    !Patterns.EMAIL_ADDRESS.matcher(binding.textInputEditTextEmail.text.toString())
                        .matches() -> {
                        toast(getString(R.string.wrong_email_type))
                        return false
                    }
                    binding.textInputEditTextPassword.text.toString().isNullOrBlank() -> {
                        toast(getString(R.string.password) + " " + getString(R.string.field_cant_be_empty))
                        return false
                    }
                    else -> {
                        when {
                            binding.textInputEditTextPassword.text.toString().length < 6 -> {
                                toast(getString(R.string.short_password))
                                return false
                            }
                            binding.textInputEditTextPassword.text.toString().length > 18 -> {
                                toast(getString(R.string.long_password))
                                return false
                            }
                            binding.textInputEditTextPassword.text.toString() != binding.textInputEditTextPasswordAgain.text.toString() -> {
                                toast(getString(R.string.did_not_match_passwords))
                                return false
                            }
                        }
                    }
                }

                return true
            }
        }

    }

    override fun getViewBinding(): ActivityRegisterBinding {
        return ActivityRegisterBinding.inflate(layoutInflater)
    }



}