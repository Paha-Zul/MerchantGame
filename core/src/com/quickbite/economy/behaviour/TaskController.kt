package com.quickbite.economy.behaviour

open class TaskController(task: Task) {

    var success: Boolean = false
        get
        private set

    var failed: Boolean = false
        get
        private set

    var running: Boolean = false
        get
        private set


    private val task: Task
        get


    init {
        this.task = task
    }

    /**
     * Safely starts the TaskController and its task. Sets the task as running.
     */
    fun SafeStart() {
        this.running = true
        this.task.start()
    }

    /**
     * Safely ends the TaskController and its task. Sets the task as not running.
     */
    fun SafeEnd() {
        this.running = false
        this.task.end()
    }

    /**
     * Sets failed as true. Ends this Task.
     */
    fun FinishWithFailure() {
        this.failed = true
        this.SafeEnd()
    }

    /**
     * Sets success as true. Ends this Task.
     */
    fun FinishWithSuccess() {
        this.success = true
        this.SafeEnd()
    }

    /**
     * Resets the TaskController and its task
     */
    open fun reset() {
        this.running = false
        this.failed = false
        this.success = false
        this.task.reset()
    }
}