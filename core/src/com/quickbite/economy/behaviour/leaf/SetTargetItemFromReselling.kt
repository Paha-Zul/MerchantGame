package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.SellingItemsComponent
import com.quickbite.economy.util.EntityListLink
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 3/13/2017.
 *
 * Attempts to set the target item (bb.targetItem) from the reselling component on the target entity (bb.targetEntity).
 * Checks to make sure the target entity exists and has a reselling component
 */
class SetTargetItemFromReselling(bb:BlackBoard) : LeafTask(bb){
    var sellingComp: SellingItemsComponent? = null

    override fun check(): Boolean {
        val workerComp = Mappers.worker[bb.myself]
        sellingComp = Mappers.selling.get(workerComp.workerBuilding)
        return sellingComp != null && sellingComp!!.resellingEntityItemLinks.size > 0
    }

    override fun start() {
        //First, get the entity list link that connects and Entity to a list of items that it's selling
        val entityListLink = sellingComp!!.resellingEntityItemLinks[sellingComp!!.index]

        validateIndex(entityListLink)

        //Set the target item.
        bb.targetItem.itemName = entityListLink.itemPriceLinkList[sellingComp!!.indexSubCounter].itemName
        bb.targetItem.itemAmount = entityListLink.itemPriceLinkList[sellingComp!!.indexSubCounter].itemPrice

        incrementCounters()

        controller.finishWithSuccess()
    }

    private fun validateIndex(entityListLink: EntityListLink){
        //If the sub counter is over the size limit, reset it
        if(sellingComp!!.indexSubCounter >= entityListLink.itemPriceLinkList.size)
            sellingComp!!.indexSubCounter = 0
    }

    private fun incrementCounters(){
        //TODO Dividing by zero problems
        sellingComp!!.index = (sellingComp!!.index + 1) % sellingComp!!.resellingEntityItemLinks.size
        sellingComp!!.indexSubCounter = (sellingComp!!.indexSubCounter + 1) % sellingComp!!.resellingEntityItemLinks[sellingComp!!.index].itemPriceLinkList.size
    }
}