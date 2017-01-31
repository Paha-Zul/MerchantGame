package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity

/**
 * Created by Paha on 1/26/2017.
 */
data class EntityListLink(val entity:Entity, val list:List<ItemPriceLink>) {
}