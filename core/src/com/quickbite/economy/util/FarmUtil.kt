package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity

/**
 * Created by Paha on 7/15/2017.
 */
object FarmUtil {

    fun harvestFarm(farmEnt: Entity){
        val fc = Mappers.farm[farmEnt]
        val ic = Mappers.inventory[farmEnt]

        fc.plantSpots.forEach { it.forEach { spot ->
            spot.plantProgress = 0f
            spot.sprite.setSize(0f, 0f)
        } }

        ic.addItem(fc.itemToGrow, fc.plantSpots.size*fc.plantSpots[0].size)
    }
}