package com.quickbite.economy.behaviour

open class LeafTask(blackboard: BlackBoard) : Task(blackboard) {

    final override val controller: TaskController = TaskController(this)
        get
}