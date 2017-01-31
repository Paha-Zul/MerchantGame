package com.quickbite.economy.components

import com.badlogic.gdx.utils.Array
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.util.EntityListLink

/**
 * Created by Paha on 1/26/2017.
 */
class ResellingItemsComponent : MyComponent {
    /**
     * A list of contracts basically
     */
    val resellingItemsList:Array<EntityListLink> = Array(5)

    override fun dispose() {

    }

    override fun initialize() {

    }
}