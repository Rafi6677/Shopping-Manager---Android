package com.example.shoppingmanager.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.shoppingmanager.R
import com.example.shoppingmanager.models.ShoppingList
import com.example.shoppingmanager.viewmodels.ShoppingListItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_shopping_lists.*
import kotlinx.android.synthetic.main.shopping_list_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class ShoppingListsActivity : AppCompatActivity() {

    val shoppingListsAdapter = GroupAdapter<ViewHolder>()

    companion object {
        const val SHOPPING_LIST_KEY = "shoppingListKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_lists)

        finishShopping_Button.setOnClickListener {
            val intent = Intent(this, AddNewShoppingListActivity::class.java)
            startActivity(intent)
        }

        showData()
    }

    private fun showData() {

        val date = Date()
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val dateText: String = formatter.format(date)

        currentDate_TextView.text = "Dziś jest:\n$dateText"

        val ref = FirebaseDatabase.getInstance().getReference("/shopping-lists")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val shoppingList = it.getValue(ShoppingList::class.java)
                    if (shoppingList != null) {
                        shoppingListsAdapter.add(ShoppingListItem(shoppingList))
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) { }
        })

        shoppingListsAdapter.setOnItemClickListener { item, _ ->
            val shoppingListItem = item as ShoppingListItem

            val intent = Intent(this, ProductsActivity::class.java)
            intent.putExtra(SHOPPING_LIST_KEY, shoppingListItem.shoppingList)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        shoppingLists_RecyclerView.adapter = shoppingListsAdapter
    }
}
