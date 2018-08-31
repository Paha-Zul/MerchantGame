package com.quickbite.economy.event.events

import com.quickbite.economy.event.GameEvent

/**
 * Created by Paha on 5/9/2017.
 * @param itemName The name of the item sold
 * @param profit The profit of the item. This is the revenue after tax is deducted from it
 * @param taxCollected The amount of tax collected from the revenue.
 */
class ItemSoldEvent(val itemName:String, val profit:Int, val taxCollected:Int) : GameEvent()