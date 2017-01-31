package com.quickbite.economy.components

import com.quickbite.economy.interfaces.MyComponent

/**
 * Created by Paha on 1/22/2017.
 */
class InitializationComponent : MyComponent {
    var initiated = false
    var initFunc:()->Unit = {}

    override fun dispose() {

    }

    override fun initialize() {

    }
}