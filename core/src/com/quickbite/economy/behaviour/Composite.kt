package com.quickbite.economy.behaviour

open class Composite(blackboard: BlackBoard, taskName: String = ""): Task(blackboard, taskName) {

    override val controller: CompositeController = CompositeController(this)
        get

    override fun start() {
        super.start()

        if (controller.taskList.isEmpty()) {
            this.controller.finishWithFailure()
        } else {
            this.controller.currTask = this.controller.taskList[this.controller.index]
        }
    }

    override fun update(delta: Float) {
        super.update(delta)

        if (this.controller.currTask == null || !this.controller.running) {
            this.controller.finishWithFailure()
            return
        }

        //If the current task is not running, go to the next task.
        if (!this.controller.currTask!!.controller.running) {
            //If the child succeeded, call child succeeded
            if (this.controller.currTask!!.controller.success) {
                this.ChildSucceeded()
            //If the child failed or it's check() fails, call child failed
            }else if (this.controller.currTask!!.controller.failed || !this.controller.currTask!!.check()) {
                this.ChildFailed()
            //Otherwise we are starting new, call new!
            } else {
                this.controller.currTask!!.controller.safeStart()
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
        return "$taskName / ${controller.currTask}"
    }
}