package com.quickbite.economy.objects

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.components.*
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/17/2017.
 */
class Workshop(sprite: Sprite, initialPosition: Vector2, dimensions:Vector2) : Entity() {

    init{
        val identityComp = IdentityComponent()
        val graphicComp = GraphicComponent()
        val transform = TransformComponent()
        val inventory = InventoryComponent()
        val building = BuildingComponent()
        val grid = GridComponent()
        val workforce = WorkForceComponent()
        val sellingItems = SellingItemsComponent()
        val init = InitializationComponent()
        val bodyComp = BodyComponent()
        val debug = DebugDrawComponent()

        graphicComp.sprite = sprite
        graphicComp.initialAnimation = true

        transform.position.set(initialPosition.x, initialPosition.y)
        transform.dimensions.set(dimensions.x, dimensions.y)

        building.buildingType = BuildingComponent.BuildingType.Workshop
        building.entranceSpotOffsets += Vector2(0f, -75f)

        grid.blockWhenPlaced = true

        workforce.numWorkerSpots = 3
//        workforce.workerTasks = listOf(listOf("haul, produce, sell"), listOf("produce", "haul, sell"), listOf("produce", "haul", "sell"))
        workforce.workerTasks = Array.with("produce", "haul", "sell")

        sellingItems.currSellingItems.add(SellingItemData("Wood Plank", 10, -1, SellingItemData.ItemSource.None))

        init.initFuncs.add({
            inventory.addItem("Wood Plank", 100)

            bodyComp.body = Util.createBody(BodyDef.BodyType.StaticBody, dimensions, initialPosition, this)
        })

        this.add(identityComp)
        this.add(graphicComp)
        this.add(transform)
        this.add(inventory)
        this.add(building)
        this.add(grid)
        this.add(workforce)
        this.add(sellingItems)
        this.add(init)
        this.add(bodyComp)
        this.add(debug)
    }
}