package com.example.shoppingmanager.viewmodels

import android.content.ClipData
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.shoppingmanager.R
import com.example.shoppingmanager.models.Product

class ProductAdapter(context: Context, val productsList: ArrayList<Product>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>(){

    private var activityItem: ItemClicked = context as ItemClicked
    private var activityItemLong: ItemLongClicked = context as ItemLongClicked

    interface ItemClicked {
        fun onItemClicked(index: Int)
    }

    interface ItemLongClicked {
        fun onItemLongClicked(index: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productName_TextView: TextView = itemView.findViewById(R.id.productName_TextView)

        init {
            itemView.setOnLongClickListener {
                activityItemLong.onItemLongClicked(productsList.indexOf(it.tag as Product))
                it.isLongClickable
            }

            itemView.setOnClickListener {
                activityItem.onItemClicked(productsList.indexOf(it.tag as Product))
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ProductAdapter.ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.add_products_row, viewGroup, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ProductAdapter.ViewHolder, i: Int) {
        viewHolder.itemView.tag = productsList[i]
        viewHolder.productName_TextView.text = productsList[i].productName
    }

    override fun getItemCount(): Int {
        return productsList.size
    }
}