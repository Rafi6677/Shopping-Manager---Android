package com.example.shoppingmanager.activities.registerlogin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.shoppingmanager.R
import com.example.shoppingmanager.activities.shopping.ShoppingListsActivity
import com.example.shoppingmanager.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        supportActionBar?.title = "Rejestracja"

        registerUser_Button.setOnClickListener {
            performRegistration()
        }

        hasAccount_TextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegistration() {
        val nick = nickInputRegister_EditText.text.toString()
        val email = emailInputRegister_EditText.text.toString()
        val password = passwordInputRegister_EditText.text.toString()
        val password2 = repeatPasswordInputRegister_EditText.text.toString()
        var phoneNumber = phoneNumberInput_EditText.text.toString()

        if(email.isEmpty() || password.isEmpty() || password2.isEmpty() || nick.isEmpty()) {
            Toast.makeText(this, "Wszystkie pola oznaczone * muszą być wypełnione.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if(phoneNumber.length != 9 && phoneNumber.isNotEmpty()) {
            phoneNumber = "0"
            Toast.makeText(this, "Nieprawidłowy numer telefonu", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if(password != password2) {
            Toast.makeText(this, "Podane hasła się różnią.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        Toast.makeText(this, "Rejestrowanie użytkownika...", Toast.LENGTH_SHORT)
            .show()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                saveUserToDatabase(phoneNumber)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Coś poszło nie tak... Spróbuj ponownie", Toast.LENGTH_SHORT)
                    .show()
                println("1")
            }
    }

    private fun saveUserToDatabase(phoneNumber: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, nickInputRegister_EditText.text.toString(), phoneNumber)

        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Rejestrowanie użytkownika...", Toast.LENGTH_SHORT)
                    .show()

                val intent = Intent(this, ShoppingListsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Coś poszło nie tak... Spróbuj ponownie", Toast.LENGTH_SHORT)
                    .show()
                println("2")
            }
    }
}
