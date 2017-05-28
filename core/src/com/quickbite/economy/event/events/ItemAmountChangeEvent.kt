package com.quickbite.economy.event.events

import com.quickbite.economy.event.GameEvent

/**
 * Created by Paha on 5/28/2017.
 */
data class ItemAmountChangeEvent(val itemName:String, val itemAmount:Int) : GameEvent() {
}