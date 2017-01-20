package com.quickbite.economy.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
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

        engine.addEntityListener(family, object: EntityListener{
            override fun entityRemoved(entity: Entity?) {

            }

            override fun entityAdded(entity: Entity?) {
                entity!!.componentBeingRemoved.add { signal, comp ->
                    if(comp is GridComponent){
                        val grid = comp
                        if(grid.blockWhenPlaced){
                            val tc = Mappers.transform.get(entity)
                            MyGame.grid.setUnblocked(tc.position.x, tc.position.y, tc.dimensions.x*0.5f, tc.dimensions.y*0.5f)
                        }
                    }
                }
            }
        })
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        //TODO Right now this only does an init() call. Maybe wasted time?
        entities.forEach { ent ->
            val gc = Mappers.grid.get(ent)
            val tc = Mappers.transform.get(ent)

            if(!gc.initiated)
                init(gc, tc)
        }
    }

    private fun init(gc:GridComponent, tc:TransformComponent){
        if(gc.blockWhenPlaced){
            MyGame.grid.setBlocked(tc.position.x, tc.position.y, tc.dimensions.x*0.5f, tc.dimensions.y*0.5f)
        }

        gc.initiated = true
    }
}