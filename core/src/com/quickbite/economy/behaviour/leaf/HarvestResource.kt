package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.ResourceComponent
import com.quickbite.economy.util.CustomTimer
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 5/24/2017.
 *
 * Harvests a resource
 */
class HarvestResource(bb:BlackBoard) : LeafTask(bb){
    lateinit var timer:CustomTimer

    val rc: ResourceComponent by lazy { Mappers.resource[bb.targetEntity]}

    override fun check(): Boolean {
        return rc.currResourceAmount > 0
    }

    override fun start() {
        super.start()

        timer = CustomTimer(rc.baseHarvestTime, 0f, true)
        timer.start()
    }

    override fun update(delta: Float) {
        super.update(delta)

        timer.update(delta)
        if(timer.done){
            val myInventory = Mappers.inventory[bb.myself] //Get my inventory
            myInventory.addItem(rc.harvestItemName, rc.harvestAmount) //Add the item to my inventory
            rc.currResourceAmount -= rc.harvestAmount //Reduce the resource amount
            if(rc.numCurrentHarvesters <= 0) Util.setResourceAsHarvested(bb.targetEntity!!, rc)
            controller.finishWithSuccess()
        }

    }
}