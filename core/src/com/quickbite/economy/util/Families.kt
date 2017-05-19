package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.*

/**
 * Created by Paha on 1/17/2017.
 */
object Families {
    val resources: ImmutableArray<Entity> = MyGame.entityEngine.getEntitiesFor(Family.all(TransformComponent::class.java, ResourceComponent::class.java)
            .exclude(PreviewComponent::class.java).get())

    val buildings: ImmutableArray<Entity> = MyGame.entityEngine.getEntitiesFor(Family.all(TransformComponent::class.java, BuildingComponent::class.java)
            .exclude(PreviewComponent::class.java).get())

    val sellingItems: ImmutableArray<Entity> = MyGame.entityEngine.getEntitiesFor(Family.all(TransformComponent::class.java, SellingItemsComponent::class.java)
            .exclude(PreviewComponent::class.java).get())

    val buildingsSellingItems: ImmutableArray<Entity> = MyGame.entityEngine.getEntitiesFor(Family.all(TransformComponent::class.java,
            SellingItemsComponent::class.java, BuildingComponent::class.java)
            .exclude(PreviewComponent::class.java).get())

    val initFamily: Family = Family.all(InitializationComponent::class.java).get()

    init{
//        MyGame.entityEngine.addEntityListener(initFamily, object:EntityListener{
//            override fun entityRemoved(p0: Entity?) {
//
//            }
//
//            override fun entityAdded(p0: Entity) {
//                val init = Mappers.init.get(p0)
//                val preview = Mappers.preview.get(p0)
//                if(preview == null) {
//                    init.initFunc()
//                    init.initiated = true
//                    p0.remove(InitializationComponent::class.java)
//                }
//            }
//        })
    }

}