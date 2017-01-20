package com.quickbite.economy.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Queue

/**
 * Created by Paha on 1/17/2017.
 */
class BuildingComponent : Component{
    enum class BuildingType{
        Shop, Workshop, House, Wall
    }

    lateinit var buildingType:BuildingType
    val entranceSpotOffsets = mutableListOf<Vector2>()
    val unitQueue = Queue<Entity>()

}