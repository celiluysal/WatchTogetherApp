package com.celiluysal.watchtogetherapp.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.celiluysal.watchtogetherapp.base.BaseActivity
import com.celiluysal.watchtogetherapp.databinding.ActivityLoginBinding
import com.celiluysal.watchtogetherapp.ui.main.MainActivity
import com.celiluysal.watchtogetherapp.ui.register.RegisterActivity

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            viewModel.login(email, password)
            viewModel.loggedIn.observe(this, Observer {
                if (it)
                    startMainActivity()
            })
        }


        binding.textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

    }

    private fun startMainActivity(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun getViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }
}