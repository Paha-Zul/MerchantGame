package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.interfaces.MyComponent

/**
 * Created by Paha on 5/18/2017.
 */
class ResourceComponent : MyComponent {
    var resourceType:String = ""
    var resourceAmount:Int = 0
    var harvestAmount = 0
    var harvestItemName = ""
    var baseHarvestTime = 0f
    var numHarvestersMax:Int = 0
    var numCurrentHarvesters:Int = 0

    override fun initialize() {

    }

    override fun dispose(myself: Entity) {

    }
}