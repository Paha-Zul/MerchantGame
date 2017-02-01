package com.quickbite.economy.objects

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.quickbite.economy.behaviour.Tasks
import com.quickbite.economy.components.*
import com.quickbite.economy.util.MutablePair
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/25/2017.
 */
class BuyerUnit(sprite: Sprite, initialPosition: Vector2, dimensions: Vector2) : Entity() {

    init{
        val graphicComp = GraphicComponent()
        val transform = TransformComponent()
        val velocity = VelocityComponent()
        val inventory = InventoryComponent()
        val behaviours = BehaviourComponent(this)
        val buyerComponent = BuyerComponent()
        val initComponent = InitializationComponent()
        val bodyComponent = BodyComponent()
        val debug = DebugDrawComponent()

//        behaviours.currTask = Tasks.buyItemFromBuilding(behaviours.blackBoard)

        graphicComp.sprite = sprite
        transform.position.set(initialPosition.x, initialPosition.y)
        transform.dimensions.set(dimensions.x, dimensions.y)

        initComponent.initFunc = {
            buyerComponent.buyList.add(MutablePair("Wood Plank", 10))
            behaviours.currTask = Tasks.buyItemFromBuilding(behaviours.blackBoard)

            bodyComponent.body = Util.createBody(BodyDef.BodyType.DynamicBody, dimensions, initialPosition, this, true)
        }

        this.add(graphicComp)
        this.add(transform)
        this.add(velocity)
        this.add(inventory)
        this.add(behaviours)
        this.add(buyerComponent)
        this.add(initComponent)
        this.add(bodyComponent)
        this.add(debug)
    }
}