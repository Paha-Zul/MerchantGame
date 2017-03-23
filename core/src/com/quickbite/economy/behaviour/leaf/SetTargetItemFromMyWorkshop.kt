package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.ProduceItemComponent
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 3/12/2017.
 *
 * Attempts to set the target item (bb.targetItem) from the blackboard's myself (bb.myself). The bb.myself value will be
 * checked for a worker component and the worker's building (worker.workerBuilding) will be checked for a production
 * component
 */
class SetTargetItemFromMyWorkshop(bb:BlackBoard) : LeafTask(bb){
    var producesItems:ProduceItemComponent? = null

    override fun check(): Boolean {
        val worker = Mappers.worker[bb.myself]
        producesItems = Mappers.produces[worker.workerBuilding]

        return producesItems != null && producesItems!!.productionList.size > 0
    }

    override fun start() {
        super.start()

        val producedItem = producesItems!!.productionList[producesItems!!.currProductionIndex]

        //Set the target item
        //TODO We need to deal with the whole array of requirements. How do? For now we random!
        val random = MathUtils.random(producedItem.requirements.size-1)
        bb.targetItem.itemName = producedItem.requirements[random].itemName
        bb.targetItem.itemAmount = producedItem.requirements[random].itemAmount

        //Increment the index
        producesItems!!.currProductionIndex = (producesItems!!.currProductionIndex + 1) % producesItems!!.productionList.size

        controller.finishWithSuccess()
    }
}