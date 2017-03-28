package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/22/2017.
 *
 * Attempts to get the closest building (of the building type) with an item in it's inventory.
 */
class GetClosestBuildingWithItem(bb:BlackBoard, val buildingType:BuildingComponent.BuildingType, var itemName:String = "", var itemAmount:Int = 1) : LeafTask(bb) {
    override fun start() {
        //If the incoming name is empty, use the blackboard's target item
        if(itemName == ""){
            itemName = bb.targetItem.itemName
            itemAmount = bb.targetItem.itemAmount

            //If the name is still empty, fail and return
            if(itemName == ""){
                controller.finishWithFailure()
                return
            }
        }

        val myWorkerBuiding = Mappers.worker[bb.myself].workerBuilding

        //Get the closest building with the item
        bb.targetEntity = Util.getClosestBuildingWithItemInInventory(Mappers.transform.get(bb.myself).position, itemName, itemAmount, hashSetOf(myWorkerBuiding!!))
        if(bb.targetEntity == null)
            controller.finishWithFailure()
        else
            controller.finishWithSuccess()
    }
}