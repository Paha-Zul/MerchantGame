package com.quickbite.economy.objects

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.quickbite.economy.components.*
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/18/2017.
 */
class Wall(sprite: Sprite, initialPosition:Vector2, dimensions:Vector2) : Entity(){
    init {
        val graphicComp = GraphicComponent()
        val transform = TransformComponent()
        val building = BuildingComponent()
        val grid = GridComponent()
        val init = InitializationComponent()
        val bodyComp = BodyComponent()

        graphicComp.sprite = sprite
        graphicComp.initialAnimation = true

        transform.position.set(initialPosition.x, initialPosition.y)
        transform.dimensions.set(dimensions.x, dimensions.y)

        building.buildingType = BuildingComponent.BuildingType.Wall

        grid.blockWhenPlaced = true

        init.initFunc = {
            bodyComp.body = Util.createBody(BodyDef.BodyType.StaticBody, dimensions, initialPosition, this)
        }

        this.add(graphicComp)
        this.add(transform)
        this.add(building)
        this.add(grid)
        this.add(bodyComp)
        this.add(init)
    }
}