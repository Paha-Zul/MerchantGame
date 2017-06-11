package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.interfaces.MyComponent

/**
 * Created by Paha on 5/18/2017.
 */
class ResourceComponent : MyComponent {
    var resourceType:String = ""
    var resourceAmount:Int = 0
    var currResourceAmount:Int = 0
    var harvestAmount = 0
    var harvestItemName = ""
    var baseHarvestTime = 0f
    var numHarvestersMax:Int = 0
    var numCurrentHarvesters:Int = 0

    /** A boolean if the resource can grow back (like trees)*/
    var canRegrow = false
    /** A range of time (in seconds) to random between for the resource to grow back*/
    var baseRegrowTime = arrayOf(180, 300)
    /** The next time that this resource should regrow*/
    var nextRegrowTime = 0f
    /** A boolean if the resource is harvested. This is useful if the resource can grow back (trees)*/
    var harvested = false
    /** The name for the harvested graphic*/
    var harvestedGraphicName = ""

    override fun initialize() {

    }

    override fun dispose(myself: Entity) {

    }
}