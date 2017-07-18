package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.FarmUtil
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 7/17/2017.
 */
class HarvestPlant(bb:BlackBoard) : LeafTask(bb) {
    override fun check(): Boolean {
        return bb.targetPlantSpot != null
    }

    override fun start() {
        super.start()
        val ic = Mappers.inventory[bb.myself]
        val fc = Mappers.farm[bb.targetEntity]

        val spot = bb.targetPlantSpot!!
        FarmUtil.harvestPlant(spot)
        ic.addItem(fc.itemToGrow)

        controller.finishWithSuccess()
    }

    override fun end() {
        super.end()
        bb.targetPlantSpot?.reseved = false
    }
}