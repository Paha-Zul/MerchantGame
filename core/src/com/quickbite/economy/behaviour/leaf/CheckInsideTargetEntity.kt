package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask

/**
 * Created by Paha on 3/27/2017.
 */
class CheckInsideTargetEntity(bb: BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()

        if(bb.targetEntity == bb.insideEntity)
            controller.finishWithSuccess()
        else
            controller.finishWithFailure()
    }
}