package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuyerComponent
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 3/12/2017.
 *
 * Attempts to set the bb.targetItem from the entity's (bb.myself) buyer component. Will check if the entity has a buyer
 * component and if the buyer has demands.
 */
class SetTargetItemFromDemand(bb:BlackBoard) : LeafTask(bb){
    var buyer:BuyerComponent? = null

    override fun check(): Boolean {
        buyer = Mappers.buyer.get(bb.myself)
        return buyer != null && buyer!!.buyList.size > 0
    }

    override fun start() {
        val itemLink = buyer!!.buyList[buyer!!.buyingIndex]

        bb.targetItem.itemName = itemLink.itemName
        bb.targetItem.itemAmount = itemLink.itemAmount

        controller.finishWithSuccess()
    }
}