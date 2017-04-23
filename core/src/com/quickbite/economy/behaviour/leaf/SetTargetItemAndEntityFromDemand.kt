package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuyerComponent
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 3/12/2017.
 *
 * Attempts to set the bb.targetItem and the bb.targetEntity by finding a building that is selling one of the
 * item demands from bb.myself (buyer component)
 */
class SetTargetItemAndEntityFromDemand(bb:BlackBoard) : LeafTask(bb){
    var buyer:BuyerComponent? = null

    override fun check(): Boolean {
        buyer = Mappers.buyer.get(bb.myself)
        return buyer != null && buyer!!.buyList.size > 0
    }

    override fun start() {
        val position = Mappers.transform[bb.myself].position

        //Search through each item to buy and see if there is a building selling it
        buyer!!.buyList.forEach {
            val building = Util.getClosestSellingItem(position, it.itemName, true, bb.entitiesToIgnore)
            if(building != null){
                bb.targetItem.itemName = it.itemName
                bb.targetItem.itemAmount = it.itemAmount
                bb.targetEntity = building
                bb.targetBuilding = Mappers.building[building]
                controller.finishWithSuccess()
                return
            }
        }

        //If we didn't finish with success in the loop above, then we failed
        controller.finishWithFailure()
    }
}