package com.quickbite.economy.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.quickbite.economy.components.GridComponent
import com.quickbite.economy.components.PreviewComponent
import com.quickbite.economy.components.TransformComponent

/**
 * Created by Paha on 1/18/2017.
 */
class GridSystem : EntitySystem(){

    lateinit var entities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)

        val family = Family.all(TransformComponent::class.java, GridComponent::class.java).exclude(PreviewComponent::class.java).get()
        entities = engine.getEntitiesFor(family)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

    }

}