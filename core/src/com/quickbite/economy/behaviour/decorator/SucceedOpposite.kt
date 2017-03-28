package com.quickbite.economy.behaviour.decorator

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.Decorator
import com.quickbite.economy.behaviour.Task

/**
 * Created by Paha on 3/28/2017.
 */
class SucceedOpposite(bb:BlackBoard, taskToDecorate: Task) : Decorator(bb, taskToDecorate) {
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