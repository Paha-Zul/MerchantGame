package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.util.objects.MutablePair
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/22/2017.
 */
class WorkerUnitComponent : MyComponent {

    var paid = false
    var dailyWage = 0
    var workerBuilding:Entity? = null
    var timeRange: MutablePair<Int, Int> = MutablePair(2, 22)
    var workDays:Array<String> = Array()
    /** The current tasks that this worker is to perform*/
    var taskList:Array<String> = Array()

    /**
     * 50+ is good, 50- is bad
     */
    var happiness = 50
        get
        set(value) { field = MathUtils.clamp(value, 0, 100) }

    override fun initialize() {

    }

    override fun dispose(myself: Entity) {
        //When we destroy this, remove ourselves from the work building
        if(workerBuilding != null)
            Mappers.workforce[workerBuilding]?.workers?.removeAll { it === myself }
    }
}