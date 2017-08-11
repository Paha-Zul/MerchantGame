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
    fun safeStart() {
        this.running = true
        this.task.start()
    }

    /**
     * Safely ends the TaskController and its task. Sets the task as not running.
     */
    open fun safeEnd() {
        this.running = false
        this.task.end()
    }

    /**
     * Sets failed as true. Ends this Task.
     */
    fun finishWithFailure() {
        this.failed = true
        this.safeEnd()
        System.out.println("[TaskController] Failed on $task (${task.failReason})")
    }

    /**
     * Sets success as true. Ends this Task.
     */
    fun finishWithSuccess() {
        this.success = true
        this.safeEnd()
    }

    /**
     * Resets the TaskController and its task
     */
    open fun safeReset() {
        this.running = false
        this.failed = false
        this.success = false
        this.task.reset()
    }
}