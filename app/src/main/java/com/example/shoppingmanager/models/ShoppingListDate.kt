package com.example.shoppingmanager.models

import java.util.*

class ShoppingListDate  {

    private val calendar = Calendar.getInstance()

    var year = calendar.get(Calendar.YEAR).toString()
    var month = calendar.get(Calendar.MONTH).toString()
    var day = calendar.get(Calendar.DAY_OF_MONTH).toString()
    var hour = calendar.get(Calendar.HOUR).toString()
    var minute = calendar.get(Calendar.MINUTE).toString()

    constructor(day: String, month: String, year: String, hour: String, minute: String) {
        this.day = day
        this.month = month
        this.year = year
        this.hour = hour
        this.minute = minute
    }

    constructor()
}