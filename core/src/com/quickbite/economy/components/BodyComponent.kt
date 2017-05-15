package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body
import com.quickbite.economy.MyGame
import com.quickbite.economy.interfaces.MyComponent

/**
 * Created by Paha on 1/30/2017.
 */
class BodyComponent : MyComponent {

    var body:Body? = null


    override fun initialize() {

    }

    override fun dispose(myself:Entity) {
        if(body != null)
            MyGame.world.destroyBody(body)
    }
}