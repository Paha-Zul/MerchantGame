package com.quickbite.economy.behaviour.leaf

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.util.objects.SellingItemData
import com.quickbite.economy.util.FindEntityUtil
import com.quickbite.economy.util.Mappers

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
    }

    /**
     * We are a shop, so we need to find stuff from our linked workshops to sell.
     */
    private fun shop(){
        val worker = Mappers.worker[bb.myself]
        val itemsReselling = Mappers.selling[worker.workerBuilding].currSellingItems
        val itemName = bb.targetItem.itemName
        val itemAmount = bb.targetItem.itemAmount

        //First, filter a list that has the item name, an Entity source, and the Entity source has the item in its inventory
        val list = itemsReselling.filter { it.itemName == itemName && it.itemSourceType == SellingItemData.ItemSource.Workshop &&
                Mappers.inventory[it.itemSourceData as Entity].hasItem(itemName) }

        //Then we will pull a random entity from that list. This helps randomize it a bit more...
        if(list.isNotEmpty()) {
            bb.targetEntity = list[MathUtils.random(list.size - 1)].itemSourceData as Entity //Get a random link and get the entity
            controller.finishWithSuccess()
        }else
            controller.finishWithFailure()
    }

    private fun workshop(myBuilding: Entity){
        val transform = Mappers.transform[bb.myself]
        val itemName = bb.targetItem.itemName
        val itemAmount = bb.targetItem.itemAmount

        //Could result in a null building
        val building = FindEntityUtil.getClosestBuildingWithOutputItemInInventory(transform.position, itemName, 1, hashSetOf(myBuilding))

        bb.targetEntity = building

        controller.finishWithSuccess()
    }
}