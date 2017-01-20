package com.quickbite.economy.behaviour

abstract class Task(blackboard: BlackBoard, var taskName: String = "Task"){

    var bb: BlackBoard = blackboard
        get
        private set

    abstract val controller: TaskController

    init {
        this.taskName = Task::class.simpleName!!
        if (taskName != "") {
            this.taskName += " - " + taskName
        }
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