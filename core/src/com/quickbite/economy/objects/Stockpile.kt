package com.quickbite.economy.objects

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.quickbite.economy.components.*
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/22/2017.
 */
class Stockpile(sprite: Sprite, initialPosition: Vector2, dimensions: Vector2) : Entity() {

    init{
        val graphicComp = GraphicComponent()
        val transform = TransformComponent()
        val inventory = InventoryComponent()
        val building = BuildingComponent()
        val grid = GridComponent()
        val initComp = InitializationComponent()
        val bodyComp = BodyComponent()
        val debug = DebugDrawComponent()

        graphicComp.sprite = sprite
        graphicComp.initialAnimation = true

        transform.position.set(initialPosition.x, initialPosition.y)
        transform.dimensions.set(dimensions.x, dimensions.y)

        building.buildingType = BuildingComponent.BuildingType.Stockpile
        building.entranceSpotOffsets += Vector2(0f, -75f)

        grid.blockWhenPlaced = true

        initComp.initFunc = {
            inventory.addItem("Wood Log", 500)
            inventory.addItem("Wood Plank", 10)

            bodyComp.body = Util.createBody(BodyDef.BodyType.StaticBody, dimensions, initialPosition, this)
        }

        this.add(graphicComp)
        this.add(transform)
        this.add(inventory)
        this.add(building)
        this.add(grid)
        this.add(initComp)
        this.add(bodyComp)
        this.add(debug)
    }
}