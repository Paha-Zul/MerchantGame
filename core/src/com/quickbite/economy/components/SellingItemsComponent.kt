package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.objects.ItemSold
import com.quickbite.economy.objects.SellingItemData

/**
 * Created by Paha on 1/19/2017.
 */
class SellingItemsComponent : MyComponent {
    var taxRate = 0.1f

    var baseSellingItems = Array<SellingItemData>()
    var currSellingItems = Array<SellingItemData>()
    val sellHistory = Array<ItemSold>()
    val goldHistory = Array<Int>()

    var isReselling = false
    /**
     * A itemPriceLinkList of contracts basically
     */
    val resellingItemsList:Array<SellingItemData> = Array(5)

    //TODO Figure out how to better keep track of counters for the links
    var indexCounter = 0
    var indexSubCounter = 0

    init{

    }

    override fun dispose(entity: Entity) {
    }

    override fun initialize() {

    }
}