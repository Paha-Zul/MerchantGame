package com.quickbite.economy.behaviour.leaf

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask

/**
 * Created by Paha on 1/25/2017.
 */
class WaitTimeOrCondition(bb:BlackBoard, val timeToWait:Float, val func:(Entity)->Boolean) : LeafTask(bb){
    var counter = 0f

    override fun update(delta: Float) {
        counter += delta
        if(func(bb.myself) || counter >= timeToWait){
            controller.finishWithSuccess()
        }
    }
}