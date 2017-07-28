package com.quickbite.economy.behaviour.decorator

import com.quickbite.economy.behaviour.Decorator
import com.quickbite.economy.behaviour.Task

/**
 * Created by Paha on 3/28/2017.
 *
 * Succeeds the opposite of what it's decorated task finished. Useful for flipping conditional tasks.
 */
class SucceedOpposite(taskToDecorate: Task) : Decorator(taskToDecorate.bb, taskToDecorate) {
    override fun update(delta: Float) {
        super.update(delta)

        //If we're done running
        if(!taskToDecorate.controller.running){
            //Flip the result
            if(taskToDecorate.controller.failed)
                controller.finishWithSuccess()
            else
                controller.finishWithFailure()
        }
    }
}