package com.example.shoppingmanager.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.shoppingmanager.R
import kotlinx.android.synthetic.main.activity_add_new_shopping_list.*
import kotlinx.android.synthetic.main.activity_shopping_lists.*

class AddNewShoppingListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_shopping_list)

        performOnLeaveListener()

        addShoppingList_Button.setOnClickListener {

        }
    }

    private fun performOnLeaveListener() {

    }
}
