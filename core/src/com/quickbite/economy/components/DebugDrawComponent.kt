package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
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
        var GLOBAL_DEBUG_WORKERS = false
        var GLOBAL_DEBUG_WORKPLACE = false
        var GLOBAL_DEBUG_PATHFINDING = false
    }

    var debugDrawPath = false
    var debugDrawCenter = false
    var debugDrawEntrace = false
    var debugDrawShopLink = false
    var debugDrawWorkplace = false
    var debugDrawWorkers = false

    override fun dispose(myself: Entity) {

    }

    override fun initialize() {

    }
}