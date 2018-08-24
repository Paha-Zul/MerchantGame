package com.quickbite.economy.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.components.FarmComponent
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 7/15/2017.
 *
 * A System to manage entities with farm components
 */
class FarmSystem(val interval:Float) : IntervalIteratingSystem(Family.all(FarmComponent::class.java).get(), interval) {
    val plantWidth = 16
    val plantHeight = 32

    override fun processEntity(ent: Entity) {
        val fc = Mappers.farm[ent]
        val plantDef = DefinitionManager.plantDefMap[fc.itemToGrow]!!
        val increment = interval/plantDef.timeToGrow

        fc.plantSpots.forEach { it.forEach { spot ->
            if(!spot.needsTending) {
                spot.plantProgress += increment
                val alpha = MathUtils.clamp(spot.plantProgress / 1f, 0f, 1f)
                spot.sprite.setSize(alpha * plantWidth, alpha * plantHeight)

                spot.needsTending = MathUtils.random(0f, 1f) <= plantDef.chanceForTend

                if(spot.plantProgress >= 1.2f)
                    spot.readyToHarvest = true
            }
        } }
    }
}