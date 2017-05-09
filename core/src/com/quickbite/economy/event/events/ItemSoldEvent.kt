package com.quickbite.economy.event.events

import com.quickbite.economy.event.GameEvent

/**
 * Created by Paha on 5/9/2017.
 */
class ItemSoldEvent(val itemName:String, val profit:Int, val taxCollected:Int) : GameEvent()