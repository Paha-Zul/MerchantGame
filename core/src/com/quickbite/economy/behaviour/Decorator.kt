package com.quickbite.economy.behaviour

open class Decorator(bb: BlackBoard, var taskToDecorate:Task, taskName:String = "") : Task(bb, taskName) {

    final override val controller: TaskController = TaskController(this)
        get

    override fun start() {
        taskToDecorate.controller.SafeStart()
    }

    override fun update(delta: Float) {
        taskToDecorate.update(delta)
    }

    override fun end() {
        taskToDecorate.controller.SafeEnd()
    }

    override fun reset() {
        taskToDecorate.controller.reset()
    }

    override fun toString(): String {
        return "$taskName/${taskToDecorate.taskName}"
    }
}