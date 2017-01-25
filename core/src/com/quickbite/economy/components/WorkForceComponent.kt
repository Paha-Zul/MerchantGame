package com.quickbite.economy.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array

/**
 * Created by Paha on 1/22/2017.
 */
class WorkForceComponent : Component{
    var numWorkerSpots:Int = 0
    var workersAvailable:Array<Entity> = Array(10)
    var workerTasks:List<List<String>> = listOf()
}