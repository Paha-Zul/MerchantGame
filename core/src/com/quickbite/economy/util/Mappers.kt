package com.quickbite.economy.util

import com.badlogic.ashley.core.ComponentMapper
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
    val workforce: ComponentMapper<WorkForceComponent> = ComponentMapper.getFor(WorkForceComponent::class.java)
    val worker: ComponentMapper<WorkerUnitComponent> = ComponentMapper.getFor(WorkerUnitComponent::class.java)
    val init: ComponentMapper<InitializationComponent> = ComponentMapper.getFor(InitializationComponent::class.java)
    val buyer: ComponentMapper<BuyerComponent> = ComponentMapper.getFor(BuyerComponent::class.java)
    val body: ComponentMapper<BodyComponent> = ComponentMapper.getFor(BodyComponent::class.java)
    val identity: ComponentMapper<IdentityComponent> = ComponentMapper.getFor(IdentityComponent::class.java)
    val produces: ComponentMapper<ProduceItemComponent> = ComponentMapper.getFor(ProduceItemComponent::class.java)
    val resource: ComponentMapper<ResourceComponent> = ComponentMapper.getFor(ResourceComponent::class.java)
    val farm: ComponentMapper<FarmComponent> = ComponentMapper.getFor(FarmComponent::class.java)
}