package com.quickbite.economy.behaviour.composite

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.Composite

class Selector (blackboard: BlackBoard, taskName: String = "") : Composite(blackboard, taskName) {

    override fun ChildSucceeded() {
        this.controller.finishWithSuccess()
    }

    override fun ChildFailed() {
        this.controller.index++

        if (this.controller.index < this.controller.taskList.size) {
            this.controller.currTask = this.controller.taskList[this.controller.index]
        } else {
            this.controller.finishWithFailure()
        }
    }
}