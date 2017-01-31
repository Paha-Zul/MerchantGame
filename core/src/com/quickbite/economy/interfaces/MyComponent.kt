package com.quickbite.economy.interfaces

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Disposable

/**
 * Created by Paha on 1/30/2017.
 */
interface MyComponent : Component, Disposable{
    fun initialize()
}