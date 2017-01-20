package com.quickbite.economy.objects

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.behaviour.BehaviourComponent
import com.quickbite.economy.behaviour.Tasks
import com.quickbite.economy.components.*

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
        val debug = DebugDrawComponent()

        behaviours.setcurrTask(Tasks.buyItemFromBuilding(behaviours.getbb()))

        graphicComp.sprite = sprite
        transform.position.set(initialPosition.x, initialPosition.y)
        transform.dimensions.set(dimensions.x, dimensions.y)

        this.add(graphicComp)
        this.add(transform)
        this.add(velocity)
        this.add(inventory)
        this.add(behaviours)
        this.add(debug)
    }
}