package com.quickbite.economy.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.quickbite.economy.components.PreviewComponent
import com.quickbite.economy.components.TransformComponent
import com.quickbite.economy.components.VelocityComponent
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/16/2017.
 */
class MovementSystem :EntitySystem(){

    lateinit var entities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)

        entities = engine.getEntitiesFor(Family.all(TransformComponent::class.java, VelocityComponent::class.java).exclude(PreviewComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        entities.forEach { ent ->
            val tm = Mappers.transform.get(ent)
            val vm = Mappers.velocity.get(ent)

            tm.position.set(tm.position.x + vm.velocity.x, tm.position.y + vm.velocity.y)
        }
    }
}