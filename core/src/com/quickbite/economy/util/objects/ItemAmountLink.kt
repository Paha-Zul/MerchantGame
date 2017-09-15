package com.quickbite.economy.util.objects

/**
 * Created by Paha on 1/31/2017.
 * A class designed to create a link between an item and and an amount of the item
 * @param itemName The name of the item
 * @param itemAmount The amount of the item
 */
data class ItemAmountLink(var itemName:String, var itemAmount:Int){
    fun set(itemName:String, itemAmount:Int){
        this.itemName = itemName
        this.itemAmount = itemAmount
    }
}