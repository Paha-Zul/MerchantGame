package com.quickbite.economy.behaviour.decorator

import com.quickbite.economy.behaviour.Decorator
import com.quickbite.economy.behaviour.Task

/**
 * Created by Paha on 1/25/2017.
 *
 * Always finishes with success regardless of what the decorated task returns. Good for optional branching.
 */
class AlwaysTrue(taskToDecorate:Task) : Decorator(taskToDecorate.bb, taskToDecorate) {

    override fun update(delta: Float) {
        super.update(delta)

        if(!taskToDecorate.controller.running)
            controller.finishWithSuccess()
    }
}