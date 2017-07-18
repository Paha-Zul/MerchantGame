package com.quickbite.economy.behaviour

import java.util.*

class CompositeController(task: Task) : TaskController(task) {
    var taskList: ArrayList<Task> = ArrayList()
    var currTask: Task? = null
    var index = 0

    fun addTask(task: Task): Task {
        taskList.add(task)
        return task
    }

    fun addTasks(vararg tasks:Task){
        tasks.forEach { taskList.add(it) }
    }

    override fun safeReset() {
        super.safeReset()
        this.currTask = null
        this.index = 0
    }

    override fun safeEnd() {
        super.safeEnd()
        taskList.forEach { it.controller.safeEnd() }
    }
}