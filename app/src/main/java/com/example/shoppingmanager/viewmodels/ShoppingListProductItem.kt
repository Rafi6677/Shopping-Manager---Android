package com.example.shoppingmanager.viewmodels

import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.example.shoppingmanager.R
import kotlinx.android.synthetic.main.working_with_shopping_list_row.view.*

class ShoppingListProductItem(val text: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.product_TextView.text = text
    }

    override fun getLayout(): Int {
        return R.layout.working_with_shopping_list_row
    }
}