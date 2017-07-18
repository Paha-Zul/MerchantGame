package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask

/**
 * Created by Paha on 7/17/2017.
 */
class TendToPlant(bb:BlackBoard) : LeafTask(bb) {
    override fun check(): Boolean {
        return bb.targetPlantSpot != null
    }

    override fun start() {
        super.start()

        //TODO Probably want to make this more involved
        bb.targetPlantSpot!!.needsTending = false

        controller.finishWithSuccess()
    }

    override fun end() {
        super.end()
        bb.targetPlantSpot?.reseved = false
    }
}