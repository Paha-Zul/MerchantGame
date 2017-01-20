package com.quickbite.economy.components

import com.badlogic.ashley.core.Component

/**
 * Created by Paha on 1/16/2017.
 */
class DebugDrawComponent : Component{
    companion object{
        var GLOBAL_DEBUG_PATH = false
        var GLOBAL_DEBUG_CENTER = false
        var GLOBAL_DEBUG_ENTRANCE = false
    }

    var debugDrawPath = false
    var debugDrawCenter = false
    var debugDrawEntrace = false
}