package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * 7/28/207
 *
 * Simply checks if bb.myself is inside its worker building already. Will fail if bb.myself is not a worker
 */
class CheckInsideMyWorkerBuilding(bb: BlackBoard) : LeafTask(bb) {

    override fun check(): Boolean {
        return Mappers.worker[bb.myself] == null
    }

    override fun start() {
        super.start()

        if(Mappers.worker[bb.myself].workerBuilding == bb.insideEntity)
            controller.finishWithSuccess()
        else
            controller.finishWithFailure()
    }
}