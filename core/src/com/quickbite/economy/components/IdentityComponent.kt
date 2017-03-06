package com.quickbite.economy.components

import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.interfaces.MyComponent

/**
 * Created by Paha on 2/1/2017.
 */
class IdentityComponent : MyComponent{
    var name:String = "Name Here"
    val uniqueID = MathUtils.random(Long.MAX_VALUE)

    override fun initialize() {

    }

    override fun dispose() {
    }
}