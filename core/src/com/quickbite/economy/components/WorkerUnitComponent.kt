package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/22/2017.
 */
class WorkerUnitComponent : MyComponent {
    var paid = false
    var dailyWage = 0
    var workerBuilding:Entity? = null

    override fun initialize() {

    }

    override fun dispose(entity: Entity) {
        if(workerBuilding != null)
            Mappers.workforce[workerBuilding].workersAvailable.removeAll { it.entity === entity }
    }
}