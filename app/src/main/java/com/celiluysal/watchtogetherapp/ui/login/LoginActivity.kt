package com.celiluysal.watchtogetherapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.base.BaseActivity
import com.celiluysal.watchtogetherapp.databinding.ActivityLoginBinding
import com.celiluysal.watchtogetherapp.ui.main.MainActivity
import com.celiluysal.watchtogetherapp.ui.register.RegisterActivity

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.buttonLogin.setOnClickListener {
            if (checkFields()) {
                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()

                viewModel.login(email, password)
                viewModel.loggedIn.observe(this, Observer {
                    if (it) {
                        startMainActivity()
                        finish()
                    }
                })
            }
        }


        binding.textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

    }

    private fun checkFields(): Boolean {
        fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

        if (binding.editTextEmail.text.toString().isNullOrBlank()) {
            toast(getString(R.string.email) + " " + getString(R.string.field_cant_be_empty))
            return false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding.editTextEmail.text.toString()).matches()){
            toast(getString(R.string.wrong_email_type))
            return false
        }

        if (binding.editTextPassword.text.toString().isNullOrBlank()) {
            toast(getString(R.string.password) + " " + getString(R.string.field_cant_be_empty))
            return false
        }
        else {
            if (binding.editTextPassword.text.toString().length < 6){
                toast(getString(R.string.short_password))
                return false
            }
            else if (binding.editTextPassword.text.toString().length > 18){
                toast(getString(R.string.long_password))
                return false
            }
        }
        return true
    }

    private fun startMainActivity(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun getViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }
}