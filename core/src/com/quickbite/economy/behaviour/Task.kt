package com.quickbite.economy.behaviour

abstract class Task(blackboard: BlackBoard, var taskName: String = ""){

    companion object {
        var DEBUG_BEHAVIOURS = false
    }

    var failReason:String = ""

    var bb: BlackBoard = blackboard
        get
        private set

    abstract val controller: TaskController

    init {
        if (taskName != "") {
            this.taskName = taskName
        }else{
            this.taskName = this.javaClass.simpleName
        }
    }

    /**
     * Call to check conditions for running the task.
     * @return True if the conditions passed and the task should run, false otherwise
     */
    open fun check():Boolean{
        return true
    }

    open fun start() {

    }

    open fun update(delta: Float) {

    }

    open fun end() {

    }

    open fun reset(){

    }

    override fun toString(): String {
        return this.taskName
    }
}