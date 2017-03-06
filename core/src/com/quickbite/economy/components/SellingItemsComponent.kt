package com.quickbite.economy.components

import com.badlogic.gdx.utils.Array
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.util.ItemPriceLink
import com.quickbite.economy.util.ItemSold

/**
 * Created by Paha on 1/19/2017.
 */
class SellingItemsComponent : MyComponent {
    var sellingItems = mutableListOf<ItemPriceLink>()
    val sellHistory = Array<ItemSold>()

    init{

    }

    override fun dispose() {

    }

    override fun initialize() {

    }
}