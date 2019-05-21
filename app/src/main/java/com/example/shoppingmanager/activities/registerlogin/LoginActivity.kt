package com.example.shoppingmanager.activities.registerlogin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.shoppingmanager.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.title = "Shopping Manager - Logowanie"
    }
}
