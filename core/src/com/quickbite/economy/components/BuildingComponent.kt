package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.interfaces.MyComponent
import java.util.*

/**
 * Created by Paha on 1/17/2017.
 */
class BuildingComponent : MyComponent {
    enum class BuildingType{
        None, Shop, Workshop, Stockpile, House, Wall
    }

    lateinit var buildingType:BuildingType
    val unitQueue = LinkedList<Entity>()

    override fun dispose(myself: Entity) {

    }

    override fun initialize() {

    }
}