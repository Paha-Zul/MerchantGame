package com.quickbite.economy.event.events

import com.quickbite.economy.event.GameEvent

/**
 * Created by Paha on 4/29/2017.
 */
class PopulationChangeEvent(val currPop:Int, val popHistory:List<Int>) : GameEvent() {
}