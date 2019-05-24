package com.example.shoppingmanager.viewmodels

import android.text.Html
import com.example.shoppingmanager.R
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

        val shoppingListInfo = "Lista z dnia:\n<b>$text</b>"

        val text1 = "Lista z dnia:\n"
        val text2 = "<b>$text</b>"

        viewHolder.itemView.shoppingListItem.text = text1 + Html.fromHtml(text2)
    }

    override fun getLayout(): Int {
        return R.layout.shopping_list_row
    }
}