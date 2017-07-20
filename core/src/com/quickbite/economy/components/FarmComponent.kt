package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.objects.FarmObject

/**
 * Created by Paha on 7/14/2017.
 */
class FarmComponent : MyComponent {
    var itemToGrow:String = "wheat"
    lateinit var plantSpots:Array<Array<FarmObject>>

    override fun initialize() {

    }

    override fun dispose(myself: Entity) {

    }
}