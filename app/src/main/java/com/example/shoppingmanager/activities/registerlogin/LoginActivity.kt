package com.example.shoppingmanager.activities.registerlogin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.shoppingmanager.R
import com.example.shoppingmanager.activities.shopping.ShoppingListsActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.title = "Shopping Manager - Logowanie"

        login_Button.setOnClickListener {
            performLogin()
        }

        backToRegistration_TextView.setOnClickListener {
            finish()
        }
    }

    private fun performLogin() {
        val email = emailInputLogin_EditText.text.toString()
        val password = passwordInputLogin_EditText.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Wszystkie pola muszą być wypełnione.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                val intent = Intent(this, ShoppingListsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Podany email lub hasło są nieprawidłowe.", Toast.LENGTH_SHORT).show()
            }
    }
}

