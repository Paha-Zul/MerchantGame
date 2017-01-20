package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.components.PreviewComponent
import com.quickbite.economy.components.SellingItemsComponent
import com.quickbite.economy.components.TransformComponent

/**
 * Created by Paha on 1/17/2017.
 */
object Families {
    val buildings: ImmutableArray<Entity> = MyGame.entityEngine.getEntitiesFor(Family.all(TransformComponent::class.java, BuildingComponent::class.java)
            .exclude(PreviewComponent::class.java).get())

    val sellingItems: ImmutableArray<Entity> = MyGame.entityEngine.getEntitiesFor(Family.all(TransformComponent::class.java, SellingItemsComponent::class.java)
            .exclude(PreviewComponent::class.java).get())
}