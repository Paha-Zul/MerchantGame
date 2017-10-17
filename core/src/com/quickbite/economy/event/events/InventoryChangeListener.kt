package com.quickbite.economy.event.events

/**
 * Created by Paha on 4/17/2017.
 */
data class InventoryChangeListener(val itemName:String, val amountChanged:Int, val finalAmount:Int)