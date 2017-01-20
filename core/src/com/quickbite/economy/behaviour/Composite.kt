package com.quickbite.economy.behaviour

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.Task
import com.quickbite.economy.behaviour.controller.CompositeController

open class Composite(blackboard: BlackBoard, taskName: String = ""): Task(blackboard, taskName) {

    override val controller: CompositeController = CompositeController(this)
        get

    override fun start() {
        super.start()

        if (controller.taskList.isEmpty()) {
            this.controller.FinishWithFailure()
        } else {
            this.controller.currTask = this.controller.taskList[0]
        }
    }

    override fun update(delta: Float) {
        super.update(delta)

        if (this.controller.currTask == null || !this.controller.running) {
            this.controller.FinishWithFailure()
            return
        }

        //If the current task is not running, go to the next task.
        if (!this.controller.currTask!!.controller.running) {
            //If we are out of tasks, end this task!
            if (this.controller.currTask!!.controller.failed) {
                this.ChildFailed()
            } else if (this.controller.currTask!!.controller.success) {
                this.ChildSucceeded()
            } else {
                this.controller.currTask!!.controller.SafeStart()
            }
        }

        //If the current task is running, update it!
        if (this.controller.currTask!!.controller.running) {
            this.controller.currTask!!.update(delta)
        }
    }

    /**
     * Logic to handle when a child fails.
     */
    protected open fun ChildFailed() {

    }

    /**
     * Logic to handle when a child succeeds.
     */
    protected open fun ChildSucceeded() {

    }

    override fun toString(): String {
        var childName = ""
        if (this.controller.currTask != null) {
            childName = this.controller.currTask.toString()
        }

        val name = this.taskName + "," + childName
        return name
    }
}