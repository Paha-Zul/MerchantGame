package com.quickbite.economy.components

import com.badlogic.gdx.utils.Array
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.util.WorkerTaskData

/**
 * Created by Paha on 1/22/2017.
 */
class WorkForceComponent : MyComponent {
    var numWorkerSpots:Int = 0
    var workersAvailable:Array<WorkerTaskData> = Array(10)
    var workerTasks:Array<String> = Array()

    override fun dispose() {

    }

    override fun initialize() {

    }
}