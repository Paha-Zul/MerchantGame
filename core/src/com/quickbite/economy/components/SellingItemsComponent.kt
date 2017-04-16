package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.util.EntityListLink
import com.quickbite.economy.util.ItemPriceLink
import com.quickbite.economy.util.ItemSold

/**
 * Created by Paha on 1/19/2017.
 */
class SellingItemsComponent : MyComponent {
    var taxRate = 0.1f

    var baseSellingItems = Array<ItemPriceLink>()
    var currSellingItems = Array<ItemPriceLink>()
    val sellHistory = Array<ItemSold>()
    val goldHistory = Array<Int>()

    var isReselling = false
    /**
     * A itemPriceLinkList of contracts basically
     */
    val resellingEntityItemLinks:Array<EntityListLink> = Array(5)

    //TODO Figure out how to better keep track of counters for the links
    var index = 0
    var indexSubCounter = 0

    init{

    }

    override fun dispose(entity: Entity) {
    }

    override fun initialize() {

    }
}