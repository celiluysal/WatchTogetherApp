package com.celiluysal.watchtogetherapp.ui.launcher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.celiluysal.watchtogetherapp.ui.login.LoginActivity
import com.celiluysal.watchtogetherapp.ui.main.MainActivity
import com.celiluysal.watchtogetherapp.utils.WTSessionManager

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        Log.e("ds", "LauncherActivity onCreate")
        super.onCreate(savedInstanceState)

        intent = Intent(this, MainActivity::class.java)

        if (WTSessionManager.shared.isLoggedIn()) {
            WTSessionManager.shared.fetchUser() { success, error ->
                if (success) {
                    intent = Intent(applicationContext, MainActivity::class.java)

                    Log.e("LauncherActivity", intent.toString())
                } else {
                    Log.e("LauncherActivity", error!!)
                    intent = Intent(this, LoginActivity::class.java)
                }
            }
        } else {
            intent = Intent(this, LoginActivity::class.java)
        }
        Log.e("LauncherActivity", intent.toString())
        startActivity(intent)
        finish()


    }

}