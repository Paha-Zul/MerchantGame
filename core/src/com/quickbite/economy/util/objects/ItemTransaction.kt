package com.quickbite.economy.util.objects

/**
 * Created by Paha on 2/1/2017.
 * A class to hold data for a transaction of an item
 * @param itemName The name of the item
 * @param itemAmount The amount of the item that was traded
 * @param pricePerItem The price per unit that the item was sold at (ie: 10 per item)
 * @param timeStamp The timestamp at which this transaction occured
 * @param buyerName The name of the buyer
 */
data class ItemTransaction(val itemName:String, val itemAmount:Int, val pricePerItem:Int, val timeStamp:Float, val buyerName:String){
    override fun toString(): String {
        return "[ItemName:$itemName, TotalAmount:$itemAmount, PricePerUnit:$pricePerItem, TimeStamp:$timeStamp, NameOfBuyer:$buyerName]"
    }
}