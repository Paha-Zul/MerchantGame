package com.quickbite.economy.behaviour.leaf

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 3/22/2017.
 */
class GetBuildingToHaulFrom(bb:BlackBoard) : LeafTask(bb){
    override fun start() {
        super.start()

        val worker = Mappers.worker[bb.myself]
        val workerBuilding = Mappers.building[worker.workerBuilding]

        when(workerBuilding.buildingType){
            BuildingComponent.BuildingType.Stockpile -> TODO()
            BuildingComponent.BuildingType.Shop -> shop()
            BuildingComponent.BuildingType.Workshop -> workshop(worker.workerBuilding!!)
            BuildingComponent.BuildingType.House -> TODO()
            BuildingComponent.BuildingType.Wall -> TODO()
            BuildingComponent.BuildingType.None -> TODO()
        }

        this.controller.finishWithSuccess()
    }

    private fun shop(){
        val worker = Mappers.worker[bb.myself]
        val links = Mappers.selling[worker.workerBuilding].resellingEntityItemLinks
        val itemName = bb.targetItem.itemName
        val itemAmount = bb.targetItem.itemAmount

        //TODO Should this actually be going through each entity link? What about our target item and target entity in the blackboard?
        //For each entity, check it's itemPriceLinkList of items
        links.forEach { (entity, itemPriceLinkList) ->
            val inventory = Mappers.inventory[entity]

            //For each item, check if the item matches what we want and the
            itemPriceLinkList.forEach { (linkedItemName) ->
                //If the item we wanted matches a link AND the building has the item, success!
                if(linkedItemName == itemName && inventory.hasItem(itemName)){
                    bb.targetEntity = entity
                    controller.finishWithSuccess()
                    return
                }
            }
        }
        controller.finishWithFailure()
    }

    private fun workshop(myBuilding: Entity){
        val transform = Mappers.transform[bb.myself]
        val itemName = bb.targetItem.itemName
        val itemAmount = bb.targetItem.itemAmount

        //Could result in a null building
        val building = Util.getClosestBuildingWithItemInInventory(transform.position, itemName, 1, hashSetOf(myBuilding))

        bb.targetEntity = building

        controller.finishWithSuccess()
    }
}