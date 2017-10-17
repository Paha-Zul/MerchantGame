package com.quickbite.economy.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.GridComponent
import com.quickbite.economy.components.PreviewComponent
import com.quickbite.economy.components.TransformComponent
import com.quickbite.economy.util.Mappers

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

        entities.forEach { ent ->
            val velocity = Mappers.velocity[ent]
            val tc = Mappers.transform[ent]
            val gc = Mappers.grid[ent]

            if(velocity != null){
                val currGridSpot = MyGame.grid.getNodeAtPosition(tc.position)!!
                if(currGridSpot.x != gc.currNodeIndex.first || currGridSpot.y != gc.currNodeIndex.second){
                    val lastGridSpot = MyGame.grid.getNodeAtIndex(gc.currNodeIndex.first, gc.currNodeIndex.second)
                    lastGridSpot?.entityList?.removeValue(ent, true)
                    currGridSpot.entityList.add(ent)
                    gc.currNodeIndex.apply { first = currGridSpot.x; second = currGridSpot.y }
                    if (MathUtils.random() < 1f)
                        currGridSpot.terrain!!.roadLevel = 1
                }
            }
        }
    }

}