package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.interfaces.MyComponent

/**
 * Created by Paha on 1/16/2017.
 */
open class VelocityComponent : MyComponent {
    var baseSpeed = 0f
    val velocity = Vector2()

    override fun dispose(myself: Entity) {

    }

    override fun initialize() {

    }
}