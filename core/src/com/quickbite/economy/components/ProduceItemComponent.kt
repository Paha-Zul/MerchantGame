package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.managers.DefinitionManager

/**
 * Created by Paha on 3/8/2017.
 * Gives an Entity the capability of producing items
 */
class ProduceItemComponent : MyComponent{

    var currProductionIndex = 0
    val productionList: Array<DefinitionManager.Production> = Array()

    override fun initialize() {

    }

    override fun dispose(myself: Entity) {
    }


}