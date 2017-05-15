package com.quickbite.economy.interfaces

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity

/**
 * Created by Paha on 1/30/2017.
 */
interface MyComponent : Component{

    fun initialize()
    fun dispose(myself: Entity)
}