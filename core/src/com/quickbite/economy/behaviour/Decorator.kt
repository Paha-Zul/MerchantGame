package com.quickbite.economy.behaviour

class Decorator(bb: BlackBoard, taskName:String = "") : Task(bb, taskName) {

    override val controller: TaskController
        get() = this.controller
}