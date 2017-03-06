package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.interfaces.MyComponent

/**
 * Created by Paha on 1/22/2017.
 */
class WorkForceComponent : MyComponent {
    var numWorkerSpots:Int = 0
    var workersAvailable:Array<Entity> = Array(10)
    var workerTasks:Array<Array<String>> = Array()

    override fun dispose() {

    }

    override fun initialize() {

    }
}