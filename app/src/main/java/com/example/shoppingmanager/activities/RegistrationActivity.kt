package com.example.shoppingmanager.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.shoppingmanager.R

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        supportActionBar?.title = "Shopping Manager - Rejestracja"
    }
}
