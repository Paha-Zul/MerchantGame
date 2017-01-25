package com.quickbite.economy.behaviour.decorator

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.Decorator
import com.quickbite.economy.behaviour.Task

/**
 * Created by Paha on 1/25/2017.
 */
class AlwaysTrue(bb:BlackBoard, taskToDecorate:Task) : Decorator(bb, taskToDecorate) {

    override fun update(delta: Float) {
        super.update(delta)

        if(!taskToDecorate.controller.running)
            controller.FinishWithSuccess()
    }
}