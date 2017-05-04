package com.quickbite.economy.managers

import com.quickbite.economy.objects.Town
import com.quickbite.economy.util.TownItemIncome

/**
 * Created by Paha on 3/25/2017.
 */
object TownManager {
    private val townMap = hashMapOf<String, Town>()

    fun init(){
        val town = Town("Town")
        town.itemIncomeMap.put("Wheat", TownItemIncome("Wheat", 75))
        town.itemIncomeMap.put("Milk", TownItemIncome("Milk", 75))
        town.itemIncomeMap.put("Wood Log", TownItemIncome("Wood Log", 20))
        town.population = 100f

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