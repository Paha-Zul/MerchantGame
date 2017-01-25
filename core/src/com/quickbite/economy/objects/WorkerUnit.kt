package com.quickbite.economy.objects

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.components.*
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/16/2017.
 */
class WorkerUnit(sprite: Sprite, initialPosition:Vector2, dimensions:Vector2) : Entity() {

    init{
        val graphicComp = GraphicComponent()
        val transform = TransformComponent()
        val velocity = VelocityComponent()
        val inventory = InventoryComponent()
        val behaviours = BehaviourComponent(this)
        val workerComponent = WorkerUnitComponent()
        val initComponent = InitializationComponent()
        val debug = DebugDrawComponent()

//        behaviours.currTask = Tasks.buyItemFromBuilding(behaviours.blackBoard)

        graphicComp.sprite = sprite
        transform.position.set(initialPosition.x, initialPosition.y)
        transform.dimensions.set(dimensions.x, dimensions.y)

        initComponent.initFunc = {
            val closestWorkshop = Util.getClosestWorkshopWithOpenWorkerPosition(transform.position)
            workerComponent.workerBuilding = closestWorkshop!!
            Mappers.workforce.get(closestWorkshop).workersAvailable.add(this)
        }

        this.add(graphicComp)
        this.add(transform)
        this.add(velocity)
        this.add(inventory)
        this.add(behaviours)
        this.add(workerComponent)
        this.add(initComponent)
        this.add(debug)
    }
}