package com.quickbite.economy.behaviour.decorator

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.Decorator
import com.quickbite.economy.behaviour.Task

/**
 * Created by Paha on 3/8/2017.
 */
class RepeatUntilFail(bb:BlackBoard, taskToDecorate: Task) : Decorator(bb, taskToDecorate) {

    override fun update(delta: Float) {
        super.update(delta)

        //If the task is not running and it did not fail, restart it!
        if(!taskToDecorate.controller.running && !taskToDecorate.controller.failed){
            taskToDecorate.controller.safeReset() //Reset the task
            taskToDecorate.controller.safeStart()  //Start the task again

        //Otherwise if we aren't running and we did fail, continue to fail
        }else if(!taskToDecorate.controller.running && taskToDecorate.controller.failed)
            this.controller.finishWithSuccess()
    }
}