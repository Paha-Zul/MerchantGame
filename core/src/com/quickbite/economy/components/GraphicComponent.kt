package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.util.objects.MutablePair

/**
 * Created by Paha on 1/16/2017.
 */

class GraphicComponent : MyComponent {
    override fun initialize() {

    }

    lateinit var sprite: Sprite
    val anchor = Vector2(0.5f, 0.5f)
    var hidden = false
    val fullyShown: Boolean
        get() = !hidden && sprite.color.a >= .95f

    var initialAnimation = false
    var animationCounter = 0f

    val moodIcons = Array<MutablePair<Sprite, Float>>(4)

    fun hide(hide: Boolean = true) {
        when (hide) {
            true -> {
                hidden = true
                sprite.setAlpha(0f)
            }
            else -> {
                hidden = false
                sprite.setAlpha(1f)
            }
        }
    }

    override fun dispose(myself: Entity) {

    }
}