package com.example.shoppingmanager.activities.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.shoppingmanager.R
import com.example.shoppingmanager.activities.shopping.ShoppingListsActivity
import com.example.shoppingmanager.models.User
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.title = "Ustawienia"

        val currentUser = ShoppingListsActivity.currentUser!!
        var phoneNumber = currentUser.phoneNumber

        if(phoneNumber != "0") {
            phoneNumberEdit_EditText.setText(phoneNumber)
            switchSendingSMSMessages_SwitchButton.isActivated = true
        } else {
            phoneNumberEdit_EditText.setText("")
        }

        saveChanges_Button.setOnClickListener {
            phoneNumber = phoneNumberEdit_EditText.text.toString()

            if(switchSendingSMSMessages_SwitchButton.isActivated) {
                if(phoneNumber.length != 9 || phoneNumber.isEmpty()) {
                    Toast.makeText(this, "Nieprawidłowy numer telefonu", Toast.LENGTH_SHORT)
                        .show()
                } else if(phoneNumber != currentUser.phoneNumber) {
                    finish()
                } else {
                    val ref = FirebaseDatabase.getInstance().getReference("/users/${currentUser.uid}")
                    val user = User(currentUser.uid, currentUser.username, phoneNumber)

                    ref.setValue(user)
                    finish()
                }
            } else {
                if(currentUser.phoneNumber == "0") {
                    finish()
                } else {
                    val ref = FirebaseDatabase.getInstance().getReference("/users/${currentUser.uid}")
                    val user = User(currentUser.uid, currentUser.username, "0")

                    ref.setValue(user)
                    finish()
                }
            }
        }
    }
}
