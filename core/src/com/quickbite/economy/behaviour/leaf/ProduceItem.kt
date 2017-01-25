package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/25/2017.
 *
 * Produces an item and adds it to the bb.targetEntity inventory
 * @param bb The BlackBoard instance
 * @param itemName The name of the item
 * @param itemAmount The amount of the item
 */
class ProduceItem(bb:BlackBoard, val itemName:String, val itemAmount:Int) : LeafTask(bb){

    override fun check(): Boolean {
        return bb.targetEntity != null
    }

    override fun start() {
        val inv = Mappers.inventory.get(bb.targetEntity)
        inv.addItem(itemName, itemAmount)
        controller.FinishWithSuccess()

        System.out.println("[ProduceItem] I produced something")
    }
}