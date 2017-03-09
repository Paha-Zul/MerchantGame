package com.quickbite.economy.components

import com.badlogic.gdx.utils.Array
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.managers.ProductionsManager

/**
 * Created by Paha on 3/8/2017.
 * Gives an Entity the capability of producing items
 */
class ProduceItemComponent : MyComponent{
    override fun initialize() {

    }

    override fun dispose() {

    }

    var currProductionCounter = 0
    val productionList:Array<ProductionsManager.Production> = Array()
}