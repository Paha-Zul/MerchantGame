package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask

/**
 * Created by Paha on 3/27/2017.
 */
class SetTargetEntityAsInside(bb: BlackBoard, val setAsOutside:Boolean = false) : LeafTask(bb){
    override fun start() {
        super.start()

        if(!setAsOutside)
            bb.insideEntity = bb.targetEntity
        else
            bb.insideEntity = null

        controller.finishWithSuccess()
    }
}