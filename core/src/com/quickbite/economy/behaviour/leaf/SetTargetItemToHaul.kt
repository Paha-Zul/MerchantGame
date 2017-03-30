package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.components.SellingItemsComponent
import com.quickbite.economy.util.EntityListLink
import com.quickbite.economy.util.ItemAmountLink
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

//        //Set the target item
//        //TODO We need to deal with the whole array of requirements. How do? For now we random!
//        val random = MathUtils.random(producedItem.requirements.size-1)
//        bb.targetItem.itemName = producedItem.requirements[random].itemName
//        bb.targetItem.itemAmount = producedItem.requirements[random].itemAmount

        //Increment the index
        producesItems.currProductionIndex = (producesItems.currProductionIndex + 1) % producesItems.productionList.size

        this.controller.finishWithSuccess()
    }

    private fun shop(){
        val worker = Mappers.worker[bb.myself]
        val sellingComp = Mappers.selling[worker.workerBuilding]

        val links = sellingComp.resellingEntityItemLinks

        //Gotta make sure we even have any links
        if(links.size > 0) {
            validateIndex(sellingComp)

            //First, get the entity list link that connects and Entity to a list of items that it's selling
            val entityListLink = sellingComp!!.resellingEntityItemLinks[sellingComp.index]

            validateSubIndex(entityListLink, sellingComp)

            if(entityListLink.itemPriceLinkList.size > 0) {
                //Set the target item.
                bb.targetItem.itemName = entityListLink.itemPriceLinkList[sellingComp.indexSubCounter].itemName
                bb.targetItem.itemAmount = entityListLink.itemPriceLinkList[sellingComp.indexSubCounter].itemPrice

                this.controller.finishWithSuccess()
            }else
                controller.finishWithFailure()

            incrementCounters(sellingComp)
        }else
            controller.finishWithFailure()
    }

    private fun validateIndex(sellingComp:SellingItemsComponent){
        if(sellingComp.index >= sellingComp.resellingEntityItemLinks.size)
            sellingComp.index = 0
    }

    private fun validateSubIndex(entityListLink: EntityListLink, sellingComp:SellingItemsComponent){


        //If the sub counter is over the size limit, reset it
        if(sellingComp.indexSubCounter >= entityListLink.itemPriceLinkList.size)
            sellingComp.indexSubCounter = 0
    }

    private fun incrementCounters(sellingComp:SellingItemsComponent){
        //TODO Dividing by zero problems

        sellingComp.index = (sellingComp.index + 1) % sellingComp.resellingEntityItemLinks.size
        if(sellingComp.resellingEntityItemLinks[sellingComp.index].itemPriceLinkList.size > 0)
            sellingComp.indexSubCounter = (sellingComp.indexSubCounter + 1) % sellingComp.resellingEntityItemLinks[sellingComp.index].itemPriceLinkList.size
    }
}