package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.interfaces.MyComponent

/**
 * Created by Paha on 1/17/2017.
 */
class TransformComponent : MyComponent {
    val position = Vector2()
    var dimensions = Vector2()

    //TODO We'll try to keep the spot map here, maybe we don't need a component for a sing hashmap?
    /** A Mapping of spot names (ie: entrance, delivery, harvesting) to an array of vector2*/
    var spotMap:HashMap<String, Array<Vector2>> = hashMapOf()

    override fun dispose(myself: Entity) {
    }

    override fun initialize() {

    }
}