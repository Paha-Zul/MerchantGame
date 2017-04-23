package com.quickbite.economy.behaviour.leaf

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
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

        //First, Find all links that are reselling the item to use AND have the item in their inventory
        val list = links.filter { it.itemPriceLinkList.any { it.itemName == itemName } && Mappers.inventory[it.entity].hasItem(itemName) }
        if(list.isNotEmpty()) {
            bb.targetEntity = list[MathUtils.random(list.size - 1)].entity //Get a random link and get the entity
            controller.finishWithSuccess()
        }else
            controller.finishWithFailure()
    }

    private fun workshop(myBuilding: Entity){
        val transform = Mappers.transform[bb.myself]
        val itemName = bb.targetItem.itemName
        val itemAmount = bb.targetItem.itemAmount

        //Could result in a null building
        val building = Util.getClosestBuildingWithOutputItemInInventory(transform.position, itemName, 1, hashSetOf(myBuilding))

        bb.targetEntity = building

        controller.finishWithSuccess()
    }
}