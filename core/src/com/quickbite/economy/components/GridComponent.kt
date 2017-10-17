package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.glutils.IndexData
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.util.objects.MutablePair

/**
 * Created by Paha on 1/18/2017.
 */

class GridComponent : MyComponent {
    var initiated = false
    var blockWhenPlaced = false
    var currNodeIndex = MutablePair(Int.MIN_VALUE, Int.MIN_VALUE)

    override fun dispose(myself: Entity) {

    }

    override fun initialize() {

    }
}