package com.quickbite.economy.components

import com.quickbite.economy.interfaces.MyComponent

/**
 * Created by Paha on 1/16/2017.
 */
class DebugDrawComponent : MyComponent {
    companion object{
        var GLOBAL_DEBUG_PATH = false
        var GLOBAL_DEBUG_CENTER = false
        var GLOBAL_DEBUG_ENTRANCE = false
        var GLOBAL_DEBUG_SHOPLINK = false
        var GLOBAL_DEBUG_BODY = false
    }

    var debugDrawPath = false
    var debugDrawCenter = false
    var debugDrawEntrace = false
    var debugDrawShopLink = false

    override fun dispose() {

    }

    override fun initialize() {

    }
}