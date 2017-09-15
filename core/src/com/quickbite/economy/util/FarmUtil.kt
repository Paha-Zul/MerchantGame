package com.quickbite.economy.util

import com.quickbite.economy.util.objects.FarmObject

/**
 * Created by Paha on 7/15/2017.
 */
object FarmUtil {
    fun harvestPlant(spot: FarmObject){
        spot.plantProgress = 0f
        spot.sprite.setSize(0f, 0f)
        spot.needsTending = true //This will act as 'replanting'
        spot.readyToHarvest = false
    }
}