package com.example.shoppingmanager.viewmodels

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.shoppingmanager.R
import com.example.shoppingmanager.models.ShoppingList
import kotlinx.android.synthetic.main.shopping_list_row.view.*
import java.text.SimpleDateFormat

class ShoppingListAdapter(context: Context, val shoppingLists: ArrayList<ShoppingList>) : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>(){

    private var activityEdit: EditButtonClicked = context as EditButtonClicked
    private var activityItem: ItemClicked = context as ItemClicked
    private var activityItemLong: ItemLongClicked = context as ItemLongClicked

    interface ItemClicked {
        fun onItemClicked(index: Int)
    }

    interface ItemLongClicked {
        fun onItemLongClicked(index: Int)
    }

    interface EditButtonClicked {
        fun onEditButtonClicked(index: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var shoppingListDate_TextView: TextView = itemView.findViewById(R.id.shoppingListDate_textView)

        init {
            itemView.setOnClickListener {
                activityItem.onItemClicked(shoppingLists.indexOf(it.tag as ShoppingList))
            }

            itemView.setOnLongClickListener {
                activityItemLong.onItemLongClicked(shoppingLists.indexOf(it.tag as ShoppingList))
                it.isLongClickable
            }

            itemView.editShoppingList_button.setOnClickListener {
                activityEdit.onEditButtonClicked(shoppingLists.indexOf(it.tag as ShoppingList))
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ShoppingListAdapter.ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.shopping_list_row, viewGroup, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ShoppingListAdapter.ViewHolder, i: Int) {
        viewHolder.itemView.tag = shoppingLists[i]
        viewHolder.itemView.editShoppingList_button.tag = shoppingLists[i]

        val date = shoppingLists[i].date
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm")
        val text: String = formatter.format(date)

        viewHolder.shoppingListDate_TextView.text = text
    }

    override fun getItemCount(): Int {
        return shoppingLists.size
    }
}