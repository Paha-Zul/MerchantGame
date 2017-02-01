package com.quickbite.economy.objects

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.quickbite.economy.components.*
import com.quickbite.economy.util.EntityListLink
import com.quickbite.economy.util.ItemPriceLink
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/19/2017.
 */
class Shop(sprite: Sprite, initialPosition: Vector2, dimensions: Vector2) : Entity() {

    init{
        val graphicComp = GraphicComponent()
        val transform = TransformComponent()
        val inventory = InventoryComponent()
        val building = BuildingComponent()
        val grid = GridComponent()
        val sellingItems = SellingItemsComponent()
        val workforce = WorkForceComponent()
        val init = InitializationComponent()
        val resell = ResellingItemsComponent()
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
        workforce.workerTasks = listOf(listOf("haul", "sell"), listOf("sell"))

        sellingItems.sellingItems += "Wood Plank"

        init.initFunc = {
            val closestWorkshop = Util.getClosestWorkshop(transform.position)
            if(closestWorkshop != null){
                //TODO Fix this hardcoding. Maybe sync with the workshops selling list, inventory, or production type?
                resell.resellingItemsList.add(EntityListLink(closestWorkshop, listOf(ItemPriceLink("Wood Plank", 15))))
            }

            bodyComp.body = Util.createBody(BodyDef.BodyType.StaticBody, dimensions, initialPosition, this)
        }

        this.add(graphicComp)
        this.add(transform)
        this.add(inventory)
        this.add(building)
        this.add(grid)
        this.add(workforce)
        this.add(sellingItems)
        this.add(init)
        this.add(resell)
        this.add(bodyComp)
        this.add(debug)
    }
}