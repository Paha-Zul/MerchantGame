package com.quickbite.economy.behaviour.leaf

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.components.SellingItemsComponent
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

        bb.targetItem.itemName = least.itemName.toLowerCase()
        bb.targetItem.itemAmount = least.itemAmount

        //Increment the indexCounter
        producesItems.currProductionIndex = (producesItems.currProductionIndex + 1) % producesItems.productionList.size

        this.controller.finishWithSuccess()
    }

    private fun shop(){
        val worker = Mappers.worker[bb.myself]
        val sellingComp = Mappers.selling[worker.workerBuilding]
        val myBuildingInventory = Mappers.inventory[worker.workerBuilding]
        val sellingItemsList = sellingComp.currSellingItems

        //Gotta make sure we even have any items
        if(sellingItemsList.size > 0) {
            //First, get the selling item and the inventory of the entity source
            val initialIndex = sellingComp.indexCounter
            val found:Boolean

            incrementIndexCounter(sellingComp)
            val sellingItem = sellingItemsList[sellingComp.indexCounter]
            found = sellingItem.itemSourceType == SellingItemData.ItemSource.Workshop

            //If we never found an item, fail and return
            if(!found){
                controller.finishWithFailure()
                return
            }

            //Get the inventory of the entity source
            val entityInventory = Mappers.inventory[sellingItem.itemSourceData as Entity]

            val workshopInvAmount = entityInventory.getItemAmount(sellingItem.itemName) //The amount the workshop has in it's inventory

            //If we want the max amount then we take all of the workshop's amount. Otherwise, we want to fulfill the stock, take the stock minus our inventory amount
            val amountToGet = if(sellingItem.itemStockAmount < 0) workshopInvAmount
                else MathUtils.clamp(sellingItem.itemStockAmount - myBuildingInventory.getItemAmount(sellingItem.itemName), 0, Int.MAX_VALUE)

            //Set the target item.
            bb.targetItem.itemName = sellingItem.itemName.toLowerCase()
            bb.targetItem.itemAmount = amountToGet

            this.controller.finishWithSuccess()
        }else
            controller.finishWithFailure()
    }

    private fun incrementIndexCounter(sellingComp:SellingItemsComponent){
        //Increment the index counter for next time (so we rotate items evenly)
        var currCounter = (sellingComp.indexCounter + 1)%sellingComp.currSellingItems.size //Get us ahead by 1 and make sure it's valid with %
        while(currCounter != sellingComp.indexCounter){
            //If our item source is from a workshop (that we are reselling from), break here
            if(sellingComp.currSellingItems[currCounter].itemSourceType == SellingItemData.ItemSource.Workshop)
                break
            //Otherwise, increment and keep going
            currCounter = (currCounter + 1)%sellingComp.currSellingItems.size
        }
        //Finally, set the index counter
        sellingComp.indexCounter = currCounter
    }
}