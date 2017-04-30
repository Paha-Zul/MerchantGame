package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask

/**
 * Created by Paha on 3/27/2017.
 */
class SetTargetEntityAsInside(bb: BlackBoard, val setAsInside:Boolean = true) : LeafTask(bb){
    override fun start() {
        super.start()

        when(setAsInside){
            true -> bb.insideEntity = bb.targetEntity
            else -> bb.insideEntity = null
        }

        controller.finishWithSuccess()
    }
}