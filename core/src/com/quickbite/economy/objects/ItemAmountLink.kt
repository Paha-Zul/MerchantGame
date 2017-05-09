package com.quickbite.economy.objects

/**
 * Created by Paha on 1/31/2017.
 */
data class ItemAmountLink(var itemName:String, var itemAmount:Int){
    fun set(itemName:String, itemAmount:Int){
        this.itemName = itemName
        this.itemAmount = itemAmount
    }
}