package com.quickbite.economy.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

/**
 * Created by Paha on 1/17/2017.
 */
class TransformComponent : Component{
    val position = Vector2()
    var dimensions = Vector2()
}