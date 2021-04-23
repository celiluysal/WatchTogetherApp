package com.celiluysal.watchtogetherapp.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.celiluysal.watchtogetherapp.Firebase.FirebaseManager
import com.celiluysal.watchtogetherapp.Firebase.RegisterRequestModel
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.base.BaseActivity
import com.celiluysal.watchtogetherapp.databinding.ActivityRegisterBinding
import com.celiluysal.watchtogetherapp.ui.login.LoginActivity
import com.celiluysal.watchtogetherapp.ui.login.LoginViewModel

class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        binding.buttonRegister.setOnClickListener {

            val fullName = binding.textInputEditTextFullName.text.toString()
            val email = binding.textInputEditTextEmail.text.toString()
            val password = binding.textInputEditTextPassword.text.toString()
            val passwordAgain = binding.textInputEditTextPasswordAgain.text.toString()
            val birthDate = "01.01.2000"

//            FirebaseManager.shared.register(RegisterRequestModel(email, password, fullName, birthDate, 1))


        }





        binding.textViewLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun getViewBinding(): ActivityRegisterBinding {
        return ActivityRegisterBinding.inflate(layoutInflater)
    }

}