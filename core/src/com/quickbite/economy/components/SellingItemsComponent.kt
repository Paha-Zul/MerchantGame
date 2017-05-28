package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.objects.ItemSold
import com.quickbite.economy.objects.SellingItemData
import com.quickbite.economy.util.CircularQueueWrapper
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/19/2017.
 */
class SellingItemsComponent : MyComponent {
    var taxRate = 0.1f

    var baseSellingItems = Array<SellingItemData>()
    var currSellingItems = Array<SellingItemData>()

    /** The items sold recently of this entity*/
    val sellHistory = CircularQueueWrapper<ItemSold>(10)
    /** The gold history of this selling entity */
    val goldHistory = CircularQueueWrapper<Int>(100)
    /** The daily income history of this selling entity*/
    val incomePerDayHistory = CircularQueueWrapper<Int>(100)

    var incomeDaily = 0

    var taxCollectedDaily = 0

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

    override fun dispose(myself: Entity) {
        resellingItemsList.toList().forEach { item ->
            Util.removeSellingItemFromReseller(this, item.itemName, item.itemSourceType, item.itemSourceData)
        }
    }

    override fun initialize() {

    }
}