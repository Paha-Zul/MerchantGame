package com.quickbite.economy.behaviour.composite

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.Composite

class Sequence (blackboard: BlackBoard, taskName: String = "") : Composite(blackboard, taskName) {

    override fun start() {
        super.start()
    }

    override fun update(delta: Float) {
        super.update(delta)
    }

    override fun end() {
        super.end()
    }

    override fun ChildFailed() {
        this.controller.finishWithFailure()
        this.controller.currTask?.controller?.safeEnd()
    }

    override fun ChildSucceeded() {
        this.controller.index++

        if (this.controller.index < this.controller.taskList.size) {
            this.controller.currTask = this.controller.taskList[this.controller.index]
        } else {
            this.controller.finishWithSuccess()
        }
    }

    override fun reset() {
        this.controller.taskList.forEach { it.controller.safeReset() } //Reset each task
        this.controller.index = 0 //Reset the indexCounter
    }
}