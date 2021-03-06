package com.example.shoppingmanager.activities.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.example.shoppingmanager.R
import com.example.shoppingmanager.activities.registerlogin.RegistrationActivity
import com.example.shoppingmanager.activities.shopping.ShoppingListsActivity
import com.example.shoppingmanager.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    companion object {
        var isSendingSmsTurnedOn = false
    }

    private var currentUser: User ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.title = "Ustawienia"

        fetchCurrentUser()

        currentUser = ShoppingListsActivity.currentUser

        var phoneNumber = currentUser!!.phoneNumber

        if(phoneNumber.length == 9) {
            phoneNumberEdit_EditText.setText(phoneNumber)
            isSendingSmsTurnedOn = true
        } else {
            phoneNumberEdit_EditText.setText("")
            isSendingSmsTurnedOn = false
        }

        switchSendingSMSMessages_SwitchButton.isChecked = isSendingSmsTurnedOn

        switchSendingSMSMessages_SwitchButton.setOnCheckedChangeListener { buttonView, isChecked ->
            when(isChecked) {
                true -> {
                    isSendingSmsTurnedOn = true
                }
                else -> {
                    isSendingSmsTurnedOn = false
                    phoneNumberEdit_EditText.setText("")
                }
            }
        }

        saveChanges_Button.setOnClickListener {
            phoneNumber = phoneNumberEdit_EditText.text.toString()

            if(isSendingSmsTurnedOn) {
                if(phoneNumber.length != 9 || phoneNumber.isEmpty()) {
                    Toast.makeText(this, "Nieprawidłowy numer telefonu", Toast.LENGTH_SHORT)
                        .show()
                } else if(phoneNumber == currentUser!!.phoneNumber) {
                    finish()
                } else {
                    val ref = FirebaseDatabase.getInstance().getReference("/users/${currentUser!!.uid}")
                    val user = User(currentUser!!.uid, currentUser!!.username, phoneNumber)

                    ref.setValue(user)
                    val previousIntent = Intent(this, ShoppingListsActivity::class.java)
                    previousIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(previousIntent)

                    finish()
                }
            } else {
                if(currentUser!!.phoneNumber == "0") {
                    finish()
                } else {
                    val ref = FirebaseDatabase.getInstance().getReference("/users/${currentUser!!.uid}")
                    val user = User(currentUser!!.uid, currentUser!!.username, "0")

                    ref.setValue(user)
                    val previousIntent = Intent(this, ShoppingListsActivity::class.java)
                    previousIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(previousIntent)

                    finish()
                }
            }
        }

        deleteAllShoppingLists_Button.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("UWAGA!")
                .setMessage("Czy na pewno chcesz się usunąć wszystkie listy zakupów?")
                .setPositiveButton("TAK") { _, _ ->
                    val ref = FirebaseDatabase.getInstance().getReference("shopping-lists/${currentUser!!.uid}")
                    ref.removeValue()

                    Toast.makeText(this, "Usunięto wszystkie listy zakupów.", Toast.LENGTH_SHORT)
                        .show()
                }
                .setNegativeButton("NIE") { _, _ ->  }
                .show()
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }
}
