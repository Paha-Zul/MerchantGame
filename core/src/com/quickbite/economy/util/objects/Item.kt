package com.quickbite.economy.util.objects

import com.badlogic.ashley.core.Entity

/**
 * Created by Paha on 4/2/2017.
 */
data class Item(val itemName:String, var itemAmount:Int, var producedAt:Entity?)