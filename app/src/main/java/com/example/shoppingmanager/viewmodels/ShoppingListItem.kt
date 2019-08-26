package com.example.shoppingmanager.viewmodels

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.text.Html
import com.example.shoppingmanager.R
import com.example.shoppingmanager.activities.shopping.ProductsActivity
import com.example.shoppingmanager.activities.shopping.ShoppingListsActivity
import com.example.shoppingmanager.models.ShoppingList
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.shopping_list_row.view.*
import java.text.SimpleDateFormat

class ShoppingListItem(val shoppingList: ShoppingList): Item<ViewHolder>() {


    override fun bind(viewHolder: ViewHolder, position: Int) {
        val date = shoppingList.date
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm")
        val text: String = formatter.format(date)

        viewHolder.itemView.shoppingListDate_textView.text = text

        viewHolder.itemView.editShoppingList_button.setOnClickListener {
        }
    }

    override fun getLayout(): Int {
        return R.layout.shopping_list_row
    }
}