package com.example.shoppingmanager.activities.shopping

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.telephony.SmsManager
import android.widget.Toast
import com.example.shoppingmanager.R
import com.example.shoppingmanager.models.ShoppingList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_new_shopping_list.*
import java.util.*
import kotlin.collections.HashMap

class AddNewShoppingListActivity : AppCompatActivity() {

    private var numberOfShoppingLists: Int = 0
    private val requestSendSms: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_shopping_list)

        supportActionBar?.title = "Nowa lista zakupów:"

        numberOfShoppingLists = intent.getIntExtra("NumberOfShoppingLists", 0)
        numberOfShoppingLists *= (-1)

        /*Toast.makeText(this, "Każdy produkt musi być dodany w odzielnej linii.", Toast.LENGTH_SHORT)
            .show()*/

        performOnLeaveListener()

        addShoppingList_Button.setOnClickListener {
            if (newShoppingList_EditText.text.isNotEmpty()) {
                val shoppingListText = newShoppingList_EditText.text.toString()
                val productsList = shoppingListText.split("\n")
                val uid = FirebaseAuth.getInstance().uid
                val id = UUID.randomUUID().toString()
                val products = HashMap<String, Boolean>()

                productsList.forEach {
                    products[it] = false
                }

                val shoppingList = ShoppingList(id, products, Date(), numberOfShoppingLists)
                val ref = FirebaseDatabase.getInstance().getReference("/shopping-lists/$uid/$id")

                ref.setValue(shoppingList)

                trySendSmsNotification()
            } else {
                Toast.makeText(this, "Pole nie może być puste.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun trySendSmsNotification() {
        val currentUser = ShoppingListsActivity.currentUser
        val phoneNumber = currentUser!!.phoneNumber

        if(phoneNumber.length == 9) {
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), requestSendSms)
            } else {
                sendSms(phoneNumber)
            }
        } else {
            ShoppingListsActivity.shoppingListsAdapter!!.notifyDataSetChanged()

            finish()
            val intent = Intent(this, ShoppingListsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val currentUser = ShoppingListsActivity.currentUser
        val phoneNumber = currentUser!!.phoneNumber

        if(requestCode == requestSendSms) {
            sendSms(phoneNumber)
        }
        else {
            finish()
            val intent = Intent(this, ShoppingListsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun sendSms(phoneNumber: String) {
        val text = "Została dodana nowa lista zakupów w aplikacji 'Shopping Manager'"

        SmsManager.getDefault().sendTextMessage(phoneNumber, null, text, null, null)
        Toast.makeText(this, "Powiadomienie sms zostało wysłane.", Toast.LENGTH_SHORT)
            .show()

        finish()
        val intent = Intent(this, ShoppingListsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun performOnLeaveListener() {
        newShoppingList_EditText.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus) {
                if(newShoppingList_EditText.text.isEmpty()) {
                    Toast.makeText(this, "Pole nie może być puste.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
