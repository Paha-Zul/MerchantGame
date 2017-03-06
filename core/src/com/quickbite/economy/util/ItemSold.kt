package com.quickbite.economy.util

/**
 * Created by Paha on 2/1/2017.
 */
data class ItemSold(val itemName:String, val itemAmount:Int, val pricePerItem:Int, val timeStamp:Float, val buyerName:String){
    override fun toString(): String {
        return "[ItemName:$itemName, TotalAmount:$itemAmount, PricePerUnit:$pricePerItem, TimeStamp:$timeStamp, NameOfBuyer:$buyerName]"
    }
}