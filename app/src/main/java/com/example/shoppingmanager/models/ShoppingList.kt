package com.example.shoppingmanager.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.HashMap

@Parcelize
class ShoppingList(val id: String, val products: HashMap<String, Boolean>, val date: Date): Parcelable {
    constructor(): this("", HashMap<String, Boolean>(), Date())
}