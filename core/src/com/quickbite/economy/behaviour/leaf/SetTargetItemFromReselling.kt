package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.SellingItemsComponent
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
        return sellingComp != null && sellingComp!!.resellingItemsList.size > 0
    }

    override fun start() {
        //First, get the item we being sold at the indicated index
        val sellingItem = sellingComp!!.resellingItemsList[sellingComp!!.indexCounter]

        //Set the target item.
        bb.targetItem.itemName = sellingItem.itemName
        bb.targetItem.itemAmount = sellingItem.itemPrice

        //Increment the index counter for next time (so we rotate items evenly)
        sellingComp!!.indexCounter += (sellingComp!!.indexCounter + 1)%sellingComp!!.resellingItemsList.size

        //Finish
        controller.finishWithSuccess()
    }
}