package com.quickbite.economy.managers

import com.quickbite.economy.objects.Town

/**
 * Created by Paha on 3/25/2017.
 */
object TownManager {
    private val townMap = hashMapOf<String, Town>()

    fun addTown(name:String, town:Town):Town{
        townMap.put(name, town)
        return town
    }

    fun getTown(name:String):Town{
        return townMap[name]!!
    }
}