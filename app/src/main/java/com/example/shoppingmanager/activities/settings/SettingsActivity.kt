package com.example.shoppingmanager.activities.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.shoppingmanager.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.title = "Ustawienia"
    }
}
