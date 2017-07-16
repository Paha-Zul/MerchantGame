package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity

/**
 * Created by Paha on 7/15/2017.
 */
object FarmUtil {

    fun harvestFarm(farmEnt: Entity){
        val fc = Mappers.farm[farmEnt]
        fc.plantSpots.forEach { it.forEach { spot ->
            spot.plantProgress = 0f
            spot.sprite.setSize(0f, 0f)
        } }
    }
}