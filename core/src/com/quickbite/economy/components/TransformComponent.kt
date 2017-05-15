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

    override fun dispose(myself: Entity) {
    }

    override fun initialize() {

    }
}