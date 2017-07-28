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
 *
 * A system that tracks gold for every entity that is selling items
 */
class GoldTrackingSystem : EntitySystem(){
    lateinit var entities: ImmutableArray<Entity>

    private var doGoldRecordFlag = false
    private var doDailyGoldClearFlag = false

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)

        entities = engine.getEntitiesFor(Family.all(SellingItemsComponent::class.java).exclude(PreviewComponent::class.java).get())

        TimeOfDay.hourlyListeners.add {
            doGoldRecordFlag = true
            if(it == 0) //If time is 0 (start of the day), then we need to clear our daily gold
                doDailyGoldClearFlag = true
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        if(doGoldRecordFlag) {
            entities.forEach { ent ->
                val sc = Mappers.selling.get(ent)
                val ic = Mappers.inventory[ent]

                val goldAmt = ic.getItemAmount("gold")
                sc.goldHistory.add(goldAmt)

                //If we are clearing the daily gold....
                if(doDailyGoldClearFlag){
                    sc.incomeDaily = 0
                    sc.taxCollectedDaily = 0
                }
            }

            doGoldRecordFlag = false
            doDailyGoldClearFlag = false
        }
    }
}