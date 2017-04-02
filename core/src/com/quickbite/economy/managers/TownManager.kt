package com.quickbite.economy.managers

import com.quickbite.economy.objects.Town
import com.quickbite.economy.util.TownItemIncome

/**
 * Created by Paha on 3/25/2017.
 */
object TownManager {
    private val townMap = hashMapOf<String, Town>()

    fun init(){
        val town = Town()
        town.itemIncomeMap.put("Wheat", TownItemIncome("Wheat", 75))
        town.itemIncomeMap.put("Milk", TownItemIncome("Milk", 75))
        town.population = 100
        TownManager.addTown("Town", town)
    }

    fun addTown(name:String, town:Town):Town{
        townMap.put(name, town)
        return town
    }

    fun getTown(name:String):Town{
        return townMap[name]!!
    }
}