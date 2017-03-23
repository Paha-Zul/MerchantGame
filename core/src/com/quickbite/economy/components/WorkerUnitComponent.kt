package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.interfaces.MyComponent

/**
 * Created by Paha on 1/22/2017.
 */
class WorkerUnitComponent : MyComponent {
    var paid = false
    var dailyWage = 0
    lateinit var workerBuilding:Entity

    override fun dispose() {

    }

    override fun initialize() {

    }
}