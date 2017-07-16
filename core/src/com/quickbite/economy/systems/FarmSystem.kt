package com.quickbite.economy.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.components.FarmComponent
import com.quickbite.economy.util.FarmUtil
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 7/15/2017.
 *
 * A System to manage entities with farm components
 */
class FarmSystem(interval:Float) : IntervalIteratingSystem(Family.all(FarmComponent::class.java).get(), interval) {
    val plantSize = 16
    val growthSpeed = 0.05f

    override fun processEntity(ent: Entity) {
        val increment = growthSpeed*interval
        val fc = Mappers.farm[ent]
        var timeToHarvest = false

        fc.plantSpots.forEach { it.forEach { spot ->
            spot.plantProgress += increment
            val alpha = MathUtils.clamp(spot.plantProgress/1f, 0f, 1f)
            spot.sprite.setSize(alpha*plantSize, alpha*plantSize)

            if(spot.plantProgress >= 1.2f)
                timeToHarvest = true
        } }

        if(timeToHarvest)
            FarmUtil.harvestFarm(ent)
    }
}