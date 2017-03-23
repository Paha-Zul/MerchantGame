package com.quickbite.economy.behaviour.leaf

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
            BuildingComponent.BuildingType.Workshop -> workshop()
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
        links.forEach { entityLink ->
            val inventory = Mappers.inventory[entityLink.entity]

            //For each item, check if the item matches what we want and the
            entityLink.itemPriceLinkList.forEach { itemLink ->
                //If the item we wanted matches a link AND the building has the item, success!
                if(itemLink.itemName == itemName && inventory.hasItem(itemName)){
                    bb.targetEntity = entityLink.entity
                    controller.finishWithSuccess()
                    return
                }
            }
        }

        controller.finishWithFailure()
    }

    private fun workshop(){
        val transform = Mappers.transform[bb.myself]
        val itemName = bb.targetItem.itemName
        val itemAmount = bb.targetItem.itemAmount

        //Could result in a null building
        val building = Util.getClosestBuildingTypeWithItem(transform.position, BuildingComponent.BuildingType.Stockpile, itemName)

        bb.targetEntity = building

        controller.finishWithSuccess()
    }
}