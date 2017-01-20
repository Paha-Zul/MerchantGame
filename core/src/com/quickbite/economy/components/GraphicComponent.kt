package com.quickbite.economy.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

/**
 * Created by Paha on 1/16/2017.
 */

class GraphicComponent : Component {
    lateinit var sprite : Sprite
    val anchor = Vector2(0.5f, 0.5f)
    var hide = false

    var initialAnimation = false
    var animationCounter = 0f
}