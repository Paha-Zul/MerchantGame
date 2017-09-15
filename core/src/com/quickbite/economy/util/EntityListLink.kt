package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.util.objects.SellingItemData

/**
 * Created by Paha on 1/26/2017.
 * Connects an Entity to a itemPriceLinkList of ItemPriceLinks.
 */
data class EntityListLink(val entity:Entity, val itemPriceLinkList:Array<SellingItemData>)