package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask

/**
 * Created by paha on 1/20/17.
 */
class LeaveBuildingQueue (bb: BlackBoard) : LeafTask(bb) {

    override fun start() {
        super.start()
        bb.targetBuilding!!.unitQueue.removeValue(bb.myself, true)
        controller.FinishWithSuccess()
    }
}