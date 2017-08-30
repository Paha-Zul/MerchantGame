package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.SellingItemsComponent
import com.quickbite.economy.objects.SellingItemData
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
        return sellingComp != null && sellingComp!!.currSellingItems.size > 0 //TODO Is this correct? (the curr selling items)
//        return sellingComp != null && sellingComp!!.resellingItemsList.size > 0
    }

    override fun start() {
        //First, get the item we being sold at the indicated index
        val sellingItem = sellingComp!!.currSellingItems[sellingComp!!.indexCounter]

        //Set the target item.
        bb.targetItem.itemName = sellingItem.itemName
        bb.targetItem.itemAmount = sellingItem.itemPrice

        increaseIndexCounter()

        //Finish
        controller.finishWithSuccess()
    }

    private fun increaseIndexCounter(){
        //Increment the index counter for next time (so we rotate items evenly)
        val comp = sellingComp!!
        var currCounter = (comp.indexCounter + 1)%comp.currSellingItems.size //Get us ahead by 1 and make sure it's valid with %
        while(currCounter != comp.indexCounter){
            //If our item source is from a workshop (that we are reselling from), break here
            if(comp.currSellingItems[currCounter].itemSourceType == SellingItemData.ItemSource.Workshop)
                break
            //Otherwise, increment and keep going
            currCounter = (currCounter + 1)%comp.currSellingItems.size
        }
        //Finally, set the index counter
        comp.indexCounter = currCounter
    }
}