package com.example.shoppingmanager.viewmodels

import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.example.shoppingmanager.R
import kotlinx.android.synthetic.main.products_to_buy_row.view.*

class ShoppingListProductToBuyItem(val text: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.productToBuy_TextView.text = text
    }

    override fun getLayout(): Int {
        return R.layout.products_to_buy_row
    }
}