package com.example.shoppingmanager.activities.shopping

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.shoppingmanager.R
import com.example.shoppingmanager.models.ShoppingList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_new_shopping_list.*
import java.util.*
import kotlin.collections.HashMap

class AddNewShoppingListActivity : AppCompatActivity() {

    var numberOfShoppingLists = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_shopping_list)

        supportActionBar?.title = "Nowa lista zakupów:"

        numberOfShoppingLists = intent.getIntExtra("NumberOfShoppingLists", 0)
        numberOfShoppingLists *= (-1)
        println(numberOfShoppingLists)

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

                finish()
                val intent = Intent(this, ShoppingListsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Pole nie może być puste.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performOnLeaveListener() {
        newShoppingList_EditText.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                if(newShoppingList_EditText.text.isEmpty()) {
                    Toast.makeText(this, "Pole nie może być puste.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
