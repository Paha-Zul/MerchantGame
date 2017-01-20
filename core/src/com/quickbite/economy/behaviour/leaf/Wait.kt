package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask

/**
 * Created by Paha on 1/17/2017.
 */
class Wait(bb:BlackBoard, val waitTime:Float = 2f) : LeafTask(bb) {
    var counter = 0f

    override fun update(delta: Float) {
        super.update(delta)

        counter += delta
        if(counter >= waitTime)
            this.controller.FinishWithSuccess()
    }
}