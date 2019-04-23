package com.example.shoppingmanager.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.shoppingmanager.R
import com.example.shoppingmanager.models.ShoppingList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_working_with_shopping_list.*
import kotlinx.android.synthetic.main.working_with_shopping_list_row.view.*

class WorkingWithShoppingListActivity : AppCompatActivity() {

    var shoppingList: ShoppingList ?= null
    var products = HashMap<String, Boolean>()

    val productsToBuyAdapter = GroupAdapter<ViewHolder>()
    val boughtProductsAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_working_with_shopping_list)

        shoppingList = intent.getParcelableExtra<ShoppingList>(ShoppingListsActivity.SHOPPING_LIST_KEY)
        products = shoppingList!!.products

        showData()
    }

    private fun showData() {
        /*
        val id = shoppingList!!.id

        val ref = FirebaseDatabase.getInstance().getReference("/shopping-lists/$id")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val shoppingList = it.getValue(ShoppingList::class.java)
                    if (shoppingList != null) {
                        for ((key, value) in shoppingList.products) {
                            if(shoppingList.products[key] == false) {
                                productsToBuyAdapter.add(ShoppingListProductItem(key))
                            } else {
                                boughtProductsAdapter.add(ShoppingListProductItem(key))
                            }
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) { }
        })
        */

        for((key, value) in products) {
            if(products[key] == false) {
                productsToBuyAdapter.add(ShoppingListProductItem(key))
            } else {
                boughtProductsAdapter.add(ShoppingListProductItem(key))
            }
        }

        productsToBuy_RecyclerView.adapter = productsToBuyAdapter
        boughtProducts_RecyclerView.adapter = boughtProductsAdapter
    }
}

class ShoppingListProductItem(val text: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.product_TextView.text = text
    }

    override fun getLayout(): Int {
        return R.layout.working_with_shopping_list_row
    }
}
