package com.quickbite.economy.behaviour.controller

import com.quickbite.economy.behaviour.Task
import com.quickbite.economy.behaviour.TaskController
import java.util.*

class CompositeController(task: Task) : TaskController(task) {
    var taskList: ArrayList<Task> = ArrayList()
    var currTask: Task? = null
    var index = 0

    fun AddTask(task: Task): Task {
        taskList.add(task)
        return task
    }

    override fun reset() {
        super.reset()
        this.currTask = null
        this.index = 0
    }
}