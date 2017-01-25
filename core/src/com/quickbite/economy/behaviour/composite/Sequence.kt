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
        this.controller.FinishWithFailure()
        this.controller.currTask?.controller?.SafeEnd()
        System.out.println("[Sequence] Failed on ${controller.currTask}")
    }

    override fun ChildSucceeded() {
        this.controller.index++

        if (this.controller.index < this.controller.taskList.size) {
            this.controller.currTask = this.controller.taskList[this.controller.index]
        } else {
            this.controller.FinishWithSuccess()
        }
    }

    override fun toString(): String {
        return "$taskName/${controller.currTask}"
    }
}