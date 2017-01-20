package com.quickbite.economy.components

import com.badlogic.ashley.core.Component

/**
 * Created by Paha on 1/19/2017.
 */
class SellingItemsComponent : Component{
    val sellingItems = mutableListOf<String>()
}