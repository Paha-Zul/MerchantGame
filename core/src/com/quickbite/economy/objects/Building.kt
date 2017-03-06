package com.quickbite.economy.objects

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.*
import com.quickbite.economy.managers.BuildingDefManager
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 2/16/2017.
 */
class Building(buildingDef: BuildingDefManager.BuildingDefinition, position:Vector2) : Entity() {

    init{
        //These are the basic needed components of every building
        val identityComp = IdentityComponent()
        val graphicComp = GraphicComponent()
        val transform = TransformComponent()
        val building = BuildingComponent()
        val grid = GridComponent()
        val init = InitializationComponent()
        val bodyComp = BodyComponent()

        identityComp.name = buildingDef.name

        graphicComp.sprite = Sprite(MyGame.manager[buildingDef.graphic, Texture::class.java])
        graphicComp.sprite.setSize(buildingDef.graphicSize[0], buildingDef.graphicSize[1])
        graphicComp.anchor.set(buildingDef.graphicAnchor[0], buildingDef.graphicAnchor[1])

        transform.dimensions.set(buildingDef.physicalDimensions[0], buildingDef.physicalDimensions[1])
        building.buildingType = Util.getBuildingType(buildingDef.buildingType)

        grid.blockWhenPlaced = buildingDef.gridBlockWhenPlaced

        init.initFuncs.add({
            bodyComp.body = Util.createBody(BodyDef.BodyType.StaticBody,  transform.dimensions, position, this)
        })

        this.add(identityComp)
        this.add(graphicComp)
        this.add(transform)
        this.add(building)
        this.add(grid)
        this.add(bodyComp)
        this.add(init)

        //These are the optional components that a building can have
        if(buildingDef.hasInventory){
            val inv = InventoryComponent()
            this.add(inv)
        }

        if(buildingDef.sellingItems.isNotEmpty()){
            val selling = SellingItemsComponent()
            selling.sellingItems = buildingDef.sellingItems.toMutableList()
            this.add(selling)
        }

        if(buildingDef.workforceMax > 0){
            val workforce = WorkForceComponent()
            workforce.numWorkerSpots = buildingDef.workforceMax
            workforce.workerTasks = buildingDef.workerTasks
            this.add(workforce)
        }

        if(buildingDef.reselling){
            val reselling = ResellingItemsComponent()
            this.add(reselling)
        }

    }
}