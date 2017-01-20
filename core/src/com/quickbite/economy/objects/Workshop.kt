package com.quickbite.economy.objects

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.components.*

/**
 * Created by Paha on 1/17/2017.
 */
class Workshop(sprite: Sprite, initialPosition: Vector2, dimensions:Vector2) : Entity() {

    init{
        val graphicComp = GraphicComponent()
        val transform = TransformComponent()
        val inventory = InventoryComponent()
        val building = BuildingComponent()
        val grid = GridComponent()
        val debug = DebugDrawComponent()

        graphicComp.sprite = sprite
        graphicComp.initialAnimation = true

        transform.position.set(initialPosition.x, initialPosition.y)
        transform.dimensions.set(dimensions.x, dimensions.y)

        inventory.addItem("Wood Plank", 100)

        building.buildingType = BuildingComponent.BuildingType.Workshop
        building.entranceSpotOffsets += Vector2(0f, -75f)

        grid.blockWhenPlaced = true

        this.add(graphicComp)
        this.add(transform)
        this.add(inventory)
        this.add(building)
        this.add(grid)
        this.add(debug)
    }
}