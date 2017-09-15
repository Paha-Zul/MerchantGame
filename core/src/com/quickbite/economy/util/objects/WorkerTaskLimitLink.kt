package com.quickbite.economy.util.objects

/**
 * Created by Paha on 5/23/2017.
 * Simple data class to link a task name to an amount
 * @param taskName The name of the task
 * @param amount The amount of the task
 */
data class WorkerTaskLimitLink(val taskName:String, var amount:Int)