package com.quickbite.economy.behaviour.leaf

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.objects.ItemAmountLink
import com.quickbite.economy.objects.SellingItemData
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 3/21/2017.
 */
class SetTargetItemToHaul(bb:BlackBoard) : LeafTask(bb){

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

    }

    private fun workshop(){
        val worker = Mappers.worker[bb.myself]
        val producesItems = Mappers.produces[worker.workerBuilding]
        val buildingInv = Mappers.inventory[worker.workerBuilding]

        val producedItem = producesItems!!.productionList[producesItems.currProductionIndex]

        //Find the item with the least amount in the inventory. This will try to keep stuff even
        val least = ItemAmountLink("", Int.MAX_VALUE)
        producedItem.requirements.forEach {
            val amt = buildingInv.getItemAmount(it.itemName)
            if(amt < least.itemAmount) {
                least.itemName = it.itemName
                least.itemAmount = it.itemAmount
            }
        }

        bb.targetItem.itemName = least.itemName
        bb.targetItem.itemAmount = least.itemAmount

        //Increment the indexCounter
        producesItems.currProductionIndex = (producesItems.currProductionIndex + 1) % producesItems.productionList.size

        this.controller.finishWithSuccess()
    }

    private fun shop(){
        val worker = Mappers.worker[bb.myself]
        val sellingComp = Mappers.selling[worker.workerBuilding]

        val sellingItemsList = sellingComp.resellingItemsList

        //Gotta make sure we even have any items
        if(sellingItemsList.size > 0) {
            //First, get the selling item and the inventory of the entity source
            var sellingItem:SellingItemData
            val initialIndex = sellingComp.indexCounter
            var found:Boolean
            //Here we need to loop through all the items to find an item selling that has an entity source
            do{
                sellingItem = sellingComp!!.resellingItemsList[sellingComp.indexCounter] //Get the selling item
                found = sellingItem.itemSourceData != null //Check if it's valid (not null)
                sellingComp.indexCounter = (sellingComp.indexCounter + 1)%sellingComp.resellingItemsList.size //Increment the index counter
            }while(sellingComp.indexCounter != initialIndex && !found) //Loop until either our index counter matches the initial index or our flag is tripped

            //If we never found an item, fail and return
            if(!found){
                controller.finishWithFailure()
                return
            }
            //Get the inventory of the entity source
            val entityInventory = Mappers.inventory[sellingItem.itemSourceData as Entity]

            //Set the target item.
            bb.targetItem.itemName = sellingItem.itemName
            bb.targetItem.itemAmount = entityInventory.getItemAmount(bb.targetItem.itemName)

            sellingComp.indexCounter = (sellingComp.indexCounter + 1)%sellingComp.resellingItemsList.size //Increment the index a final time

            this.controller.finishWithSuccess()
        }else
            controller.finishWithFailure()
    }
}