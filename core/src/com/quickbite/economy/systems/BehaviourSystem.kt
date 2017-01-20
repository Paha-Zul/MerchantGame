package com.quickbite.economy.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.quickbite.economy.behaviour.BehaviourComponent
import com.quickbite.economy.components.PreviewComponent
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/16/2017.
 */

class BehaviourSystem : EntitySystem() {
    lateinit var entities:ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)

        entities = engine.getEntitiesFor(Family.all(BehaviourComponent::class.java).exclude(PreviewComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        entities.forEach { ent ->
            val bm = Mappers.behaviour.get(ent)

            if (!bm.CurrTaskDone()) {
                bm.currTask.update(0.016f)
                bm.currTaskName = bm.currTask.toString().split("[,]".toRegex()).toTypedArray()

                if (!bm.currTask.controller.running) {
                    bm.onCompletionCallback?.invoke()
                }
            }
        }
    }


}