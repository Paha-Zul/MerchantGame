package com.quickbite.economy.objects

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.components.*
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/19/2017.
 */
class Shop(sprite: Sprite, initialPosition: Vector2, dimensions: Vector2) : Entity() {

    init{
        val identityComp = IdentityComponent()
        val graphicComp = GraphicComponent()
        val transform = TransformComponent()
        val inventory = InventoryComponent()
        val building = BuildingComponent()
        val grid = GridComponent()
        val sellingItems = SellingItemsComponent()
        val workforce = WorkForceComponent()
        val init = InitializationComponent()
        val bodyComp = BodyComponent()
        val debug = DebugDrawComponent()

        graphicComp.sprite = sprite
        graphicComp.initialAnimation = true

        transform.position.set(initialPosition.x, initialPosition.y)
        transform.dimensions.set(dimensions.x, dimensions.y)

        building.buildingType = BuildingComponent.BuildingType.Shop
        building.entranceSpotOffsets += Vector2(0f, -50f)

        grid.blockWhenPlaced = true

        workforce.numWorkerSpots = 5
//        workforce.workerTasks = listOf(listOf("haul", "sell"), listOf("sell"))
        workforce.workerTasks = Array.with("haul", "sell")

        sellingItems.currSellingItems.add(SellingItemData("Wood Plank", 10, -1, SellingItemData.ItemSource.None))

        init.initFuncs.add({
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