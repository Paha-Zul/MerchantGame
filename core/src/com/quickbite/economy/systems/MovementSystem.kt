package com.quickbite.economy.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.quickbite.economy.MyGame
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

        if(deltaTime <= 0)
            return

        entities.forEach { ent ->
            val tm = Mappers.transform.get(ent)
            val vm = Mappers.velocity.get(ent)
            val bc = Mappers.body.get(ent)

            if(bc == null)
                tm.position.set(tm.position.x + vm.velocity.x, tm.position.y + vm.velocity.y)
            else{
                bc.body!!.setLinearVelocity(vm.velocity.x*50, vm.velocity.y*50)
                tm.position.set(bc.body!!.position.x, bc.body!!.position.y)
            }
        }

        MyGame.world.step(1/60f, 6, 2)
    }
}