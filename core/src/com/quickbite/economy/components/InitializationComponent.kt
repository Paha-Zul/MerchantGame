package com.quickbite.economy.components

import com.badlogic.ashley.core.Component

/**
 * Created by Paha on 1/22/2017.
 */
class InitializationComponent : Component{
    var initiated = false
    var initFunc:()->Unit = {}
}