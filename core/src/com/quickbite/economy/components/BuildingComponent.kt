package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
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
    val entranceSpotOffsets = mutableListOf<Vector2>()
    val unitQueue = LinkedList<Entity>()

    override fun dispose() {

    }

    override fun initialize() {

    }
}