package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.interfaces.MyComponent

/**
 * Created by Paha on 1/22/2017.
 */
class InitializationComponent : MyComponent {
    var initiated = false
    var initFuncs:MutableList<()->Unit> = mutableListOf()

    override fun dispose(entity: Entity) {

    }

    override fun initialize() {

    }
}