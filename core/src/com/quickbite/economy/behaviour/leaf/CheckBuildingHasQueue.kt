package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/25/2017.
 *
 * Checks if the bb.targetEntity's building has any unit in the unit queue
 */
class CheckBuildingHasQueue(bb:BlackBoard) : LeafTask(bb){
    override fun check(): Boolean {
        return Mappers.building.get(bb.targetEntity).unitQueue.size > 0
    }

    override fun start() {
        controller.finishWithSuccess()
    }
}