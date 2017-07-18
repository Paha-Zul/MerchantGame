package com.quickbite.economy.behaviour.decorator

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.Decorator
import com.quickbite.economy.behaviour.Task

/**
 * Created by Paha on 7/18/2017.
 *
 * Repeats a task a number of times as long as it's succeeding. Stops when the task fails
 * @param bb The Blackboard to use
 * @param numTimes The number of times to repeat the task
 * @param taskToDecorate The task to decorate/repeat
 */
class RepeatTaskNumberOfTimes(bb:BlackBoard, val numTimes:Int, taskToDecorate: Task) : Decorator(bb, taskToDecorate)  {
    var counter = 0

    override fun update(delta: Float) {
        super.update(delta)

        //If the task is not running and it did not fail, restart it!
        if(!taskToDecorate.controller.running && !taskToDecorate.controller.failed && counter < numTimes){
            taskToDecorate.controller.safeReset() //Reset the task
            taskToDecorate.controller.safeStart()  //Start the task again
            counter++

        //Otherwise if we aren't running and we did fail, continue to fail
        }else if(!taskToDecorate.controller.running && taskToDecorate.controller.failed)
            this.controller.finishWithFailure()

        //If we hit our number of times to repeat, finish with success yo
        else if(counter >= numTimes){
            this.controller.finishWithSuccess()
        }
    }
}