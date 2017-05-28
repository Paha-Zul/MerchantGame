package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask

/**
 * Created by Paha on 3/27/2017.
 *
 * Sets this entity as inside a building.
 * @param setAsInside If true, the bb.targetEntity will be assigned to bb.insideEntity for future use/checking. If false,
 * the bb.insideEntity is set to null
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