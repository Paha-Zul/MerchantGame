package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.behaviour.Tasks
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.objects.WorkerTaskLimitLink
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/22/2017.
 */
class WorkForceComponent : MyComponent {
    var numWorkerSpots:Int = 0
    var workersAvailable:Array<Entity> = Array(10)

    /** The worker tasks and their limits*/
    var workerTasksLimits:Array<WorkerTaskLimitLink> = Array()
    var workersPaidFlag = false

    val workerTaskMap : HashMap<String, Array<Entity>> = hashMapOf()

    override fun initialize() {

    }

    override fun dispose(myself: Entity) {
        workersAvailable.forEach {
            Util.removeWorkerFromBuilding(it, myself)
            val beh = Mappers.behaviour[it]
            beh.currTask = Tasks.leaveMap(beh.blackBoard)
        }
    }
}