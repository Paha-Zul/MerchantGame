package com.quickbite.economy.util

import com.badlogic.ashley.core.ComponentMapper
import com.quickbite.economy.behaviour.BehaviourComponent
import com.quickbite.economy.components.*

/**
 * Created by Paha on 1/16/2017.
 */

object Mappers{
    val inventory: ComponentMapper<InventoryComponent>  = ComponentMapper.getFor(InventoryComponent::class.java)
    val behaviour: ComponentMapper<BehaviourComponent> = ComponentMapper.getFor(BehaviourComponent::class.java)
    val transform: ComponentMapper<TransformComponent> = ComponentMapper.getFor(TransformComponent::class.java)
    val graphic: ComponentMapper<GraphicComponent> = ComponentMapper.getFor(GraphicComponent::class.java)
    val velocity: ComponentMapper<VelocityComponent> = ComponentMapper.getFor(VelocityComponent::class.java)
    val building: ComponentMapper<BuildingComponent> = ComponentMapper.getFor(BuildingComponent::class.java)
    val grid: ComponentMapper<GridComponent> = ComponentMapper.getFor(GridComponent::class.java)
    val preview: ComponentMapper<PreviewComponent> = ComponentMapper.getFor(PreviewComponent::class.java)
    val debugDraw: ComponentMapper<DebugDrawComponent> = ComponentMapper.getFor(DebugDrawComponent::class.java)
    val selling: ComponentMapper<SellingItemsComponent> = ComponentMapper.getFor(SellingItemsComponent::class.java)
}