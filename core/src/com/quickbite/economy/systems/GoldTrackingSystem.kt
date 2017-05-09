package com.quickbite.economy.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.quickbite.economy.components.PreviewComponent
import com.quickbite.economy.components.SellingItemsComponent
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.TimeOfDay

/**
 * Created by Paha on 5/7/2017.
 */
class GoldTrackingSystem : EntitySystem(){
    lateinit var entities: ImmutableArray<Entity>

    private var doGoldRecordFlag = false

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)

        entities = engine.getEntitiesFor(Family.all(SellingItemsComponent::class.java).exclude(PreviewComponent::class.java).get())

        TimeOfDay.hourlyListeners.add { doGoldRecordFlag = true }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        if(!doGoldRecordFlag)
            return

        entities.forEach { ent ->
            val sc = Mappers.selling.get(ent)
            val ic = Mappers.inventory[ent]

            val goldAmt = ic.getItemAmount("Gold")
            sc.goldHistory.add(goldAmt)
        }

        doGoldRecordFlag = false
    }
}