package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.ProduceItemComponent
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 3/12/2017.
 *
 * Attempts to get a production from the target entity (bb.targetEntity) and set the target item (bb.targetItem) for future use.
 * Fails if bb.targetEntity does not contain a production component
 */
class SetTargetItemFromTargetWorkshopProduction(bb:BlackBoard) : LeafTask(bb){
    var producesItems:ProduceItemComponent? = null

    override fun check(): Boolean {
        producesItems = Mappers.produces[bb.targetEntity]
        return producesItems != null && producesItems!!.productionList.size > 0
    }

    override fun start() {
        super.start()

        val producedItem = producesItems!!.productionList[producesItems!!.currProductionIndex]

        //TODO We need to deal with the whole array of requirements. How do?
        bb.targetItem.itemName = producedItem.requirements[0].itemName
        bb.targetItem.itemAmount = producedItem.requirements[0].itemAmount

        controller.finishWithSuccess()
    }
}