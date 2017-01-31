package com.quickbite.economy.components

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.interfaces.MyComponent

/**
 * Created by Paha on 1/17/2017.
 */
class TransformComponent : MyComponent {
    val position = Vector2()
    var dimensions = Vector2()

    override fun dispose() {

    }

    override fun initialize() {

    }
}