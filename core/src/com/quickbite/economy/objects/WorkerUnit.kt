package com.quickbite.economy.objects

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.components.*
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Names
import com.quickbite.economy.util.Util
import com.quickbite.economy.util.WorkerTaskData

/**
 * Created by Paha on 1/16/2017.
 */
class WorkerUnit(sprite: Sprite, initialPosition:Vector2, dimensions:Vector2) : Entity() {

    init{
        val identityComp = IdentityComponent()
        val graphicComp = GraphicComponent()
        val transform = TransformComponent()
        val velocity = VelocityComponent()
        val inventory = InventoryComponent()
        val behaviours = BehaviourComponent(this)
        val workerComponent = WorkerUnitComponent()
        val initComponent = InitializationComponent()
        val bodyComponent = BodyComponent()
        val debug = DebugDrawComponent()

        identityComp.name = Names.randomName

        graphicComp.sprite = sprite
        transform.position.set(initialPosition.x, initialPosition.y)
        transform.dimensions.set(dimensions.x, dimensions.y)

        initComponent.initFuncs.add({
            val closestWorkshop = Util.getClosestBuildingWithWorkerPosition(transform.position)

            //TODO Figure out what to do if this workshop is null?
            if(closestWorkshop != null) {
                workerComponent.workerBuilding = closestWorkshop
                Mappers.workforce.get(closestWorkshop).workersAvailable.add(WorkerTaskData(this, Array(), Pair(7, 20), Array()))
            }

            behaviours.blackBoard.myself = this
            bodyComponent.body = Util.createBody(BodyDef.BodyType.DynamicBody, dimensions, initialPosition, this, true)
        })

        this.add(identityComp)
        this.add(transform)
        this.add(velocity)
        this.add(inventory)
        this.add(behaviours)
        this.add(workerComponent)
        this.add(initComponent)
        this.add(bodyComponent)
        this.add(graphicComp)
        this.add(debug)
    }
}