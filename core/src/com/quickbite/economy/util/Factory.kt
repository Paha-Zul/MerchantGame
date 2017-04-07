package com.quickbite.economy.util

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.MyGame
import com.quickbite.economy.behaviour.Tasks
import com.quickbite.economy.components.*
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.managers.BuildingDefManager
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.managers.ItemDefManager
import com.quickbite.economy.managers.ProductionsManager
import com.quickbite.economy.objects.*

/**
 * Created by Paha on 12/13/2016.
 */
object Factory {

    fun createObject(type:String, position:Vector2, compsToAdd:List<Component> = listOf()):Entity? {
        var thing:Entity? = null
        val dimensions:Vector2 = Vector2()

        //TODO Figure out how to use dimensions better. Right now they are hardcoded for prototyping
        when(type){
            "lumberyard" -> {
                dimensions.set(125f, 125f)
                val sprite = Sprite(MyGame.manager["workshop", Texture::class.java])
                sprite.setSize(dimensions.x, dimensions.y)

                thing = Workshop(sprite, position, dimensions)
            }

            "shop" -> {
                dimensions.set(75f, 75f)
                val sprite = Sprite(MyGame.manager["market", Texture::class.java])
                sprite.setSize(dimensions.x, dimensions.y)

                thing = Shop(sprite, position, dimensions)
            }

            "wall" -> {
                dimensions.set(50f, 50f)
                val sprite = Sprite(MyGame.manager["palisade_wall_horizontal", Texture::class.java])
                sprite.setSize(dimensions.x, dimensions.y)

                thing = Wall(sprite, position, dimensions)
            }

            "stockpile" -> {
                dimensions.set(90f, 90f)
                val sprite = Sprite(MyGame.manager["stockpile", Texture::class.java])
                sprite.setSize(dimensions.x, dimensions.y)

                thing = Stockpile(sprite, position, dimensions)
            }

            "worker" -> {
                dimensions.set(20f, 20f)
                val sprite = Sprite(MyGame.manager["seller", Texture::class.java])
                sprite.setSize(dimensions.x, dimensions.y)

                thing = WorkerUnit(sprite, position, dimensions)
            }

            "buyer" -> {
                dimensions.set(20f, 20f)
                val sprite = Sprite(MyGame.manager["buyer", Texture::class.java])
                sprite.setSize(dimensions.x, dimensions.y)

                thing = BuyerUnit(sprite, position, dimensions)
            }
        }

        if(thing != null) {
            compsToAdd.forEach { thing!!.add(it) }
            thing.components.forEach { comp -> (comp as MyComponent).initialize() }
            MyGame.entityEngine.addEntity(thing)
        }

        return thing
    }

    fun createBuilding(name:String, position:Vector2, compsToAdd:List<Component> = listOf()):Entity? {
        val thing:Entity? = Building(BuildingDefManager.buildingDefsMap[name.toLowerCase()]!!, position)

        if(thing != null) {
            compsToAdd.forEach { thing.add(it) }
            thing.components.forEach { comp -> (comp as MyComponent).initialize() }
            MyGame.entityEngine.addEntity(thing)
        }

        return thing
    }

    fun createObjectFromJson(name:String, position:Vector2, compsToAdd:List<Component> = listOf()):Entity? {
        val name = name.toLowerCase()
        val entity:Entity = Entity()
        val definition = DefinitionManager.definitionMap[name]!!

        //These are the basic needed components of every building
        val identityComp = IdentityComponent()
        val graphicComp = GraphicComponent()
        val transform = TransformComponent()
        val grid = GridComponent()
        val init = InitializationComponent()
        val debug = DebugDrawComponent()

        identityComp.name = definition.name

        graphicComp.sprite = Sprite(MyGame.manager[definition.graphicDef.graphicName, Texture::class.java])
        graphicComp.sprite.setSize(definition.graphicDef.graphicSize[0], definition.graphicDef.graphicSize[1])
        graphicComp.anchor.set(definition.graphicDef.graphicAnchor[0], definition.graphicDef.graphicAnchor[1])

        transform.dimensions.set(definition.physicalDimensions[0], definition.physicalDimensions[1])
        transform.position.set(position)

        grid.blockWhenPlaced = definition.gridBlockWhenPlaced

        init.initFuncs.add({

        })

        entity.add(identityComp)
        entity.add(graphicComp)
        entity.add(transform)
        entity.add(grid)
        entity.add(init)
        entity.add(debug)

        //Check for building definition
        val buildingType = Util.getBuildingType(definition.buildingDef.buildingType)

        if(buildingType != BuildingComponent.BuildingType.None){
            val building = BuildingComponent()
            building.buildingType = buildingType
            definition.buildingDef.entranceSpots.forEach { spot ->
                building.entranceSpotOffsets += spot
            }
            entity.add(building)
        }

        //These are the optional components that a building can have
        if(definition.inventoryDef.hasInventory){
            val inv = InventoryComponent()
            definition.inventoryDef.debugItemList.forEach { item ->
                inv.addItem(item.itemName, item.itemAmount)
            }
            entity.add(inv)
        }

        //Check for selling items definition
        if(definition.sellingItems.isSelling){
            val selling = SellingItemsComponent()

            //Things like this have to be copied or else they are linked and can be modified!!!
            val sellingList = Array<ItemPriceLink>()
            definition.sellingItems.sellingList.forEach { (itemName) ->
                sellingList.add(ItemPriceLink(itemName, ItemDefManager.itemDefMap[itemName]!!.baseMarketPrice))
            }

            selling.baseSellingItems = sellingList //Use the base array here
            selling.currSellingItems = Array(sellingList) //Copy it for this one
            selling.isReselling = definition.sellingItems.isReselling
            selling.taxRate = definition.sellingItems.taxRate
            entity.add(selling)
        }

        //Check for workforce definition
        if(definition.workforceDef.workforceMax > 0){
            val workforce = WorkForceComponent()
            workforce.numWorkerSpots = definition.workforceDef.workforceMax
            workforce.workerTasks = definition.workforceDef.workerTasks
            entity.add(workforce)
        }

        //Check for behaviour definition
        if(definition.hasBehaviours){
            val behaviour = BehaviourComponent(entity)
            entity.add(behaviour)
        }

        //Check for velocity component
        if(definition.velocityDef.hasVelocity){
            val velocity = VelocityComponent()
            velocity.baseSpeed = definition.velocityDef.baseSpeed
            entity.add(velocity)
        }

        if(definition.physicsDef.hasPhysicsBody){
            val bodyComp = BodyComponent()
            init.initFuncs.add { bodyComp.body = Util.createBody(definition.physicsDef.bodyType,
                    Vector2(transform.dimensions.x*Constants.BOX2D_SCALE, transform.dimensions.y*Constants.BOX2D_SCALE),
                    Vector2(transform.position.x*Constants.BOX2D_SCALE, transform.position.y*Constants.BOX2D_SCALE), entity, true) }

            entity.add(bodyComp)
        }

        if(definition.productionDef.produces.size > 0){
            val producesItems = ProduceItemComponent()
            definition.productionDef.produces.forEach { itemName ->
                producesItems.productionList.add(ProductionsManager.productionMap[itemName])
            }

            entity.add(producesItems)

            //We need to add the input and output items for the inventory from the productions
            init.initFuncs.add {
                val inventory = Mappers.inventory[entity]

                //make sure we have an inventory
                if(inventory != null){
                    //Clear both of these since they are initially set to "All" at class creation.
                    inventory.inputItems.clear()
                    inventory.outputItems.clear()

                    //for each production, add the produced item to the output and all the requirements to the input
                    producesItems.productionList.forEach { production ->
                        inventory.outputItems += production.producedItem
                        production.requirements.forEach { (itemName) ->
                            inventory.inputItems += itemName
                        }
                    }
                }
            }
        }

        if(definition.isWorker){
            val workerUnit = WorkerUnitComponent()
            workerUnit.dailyWage = MathUtils.random(50, 300)
            entity.add(workerUnit)

            init.initFuncs.add({
                val closestWorkshop = Util.getClosestBuildingWithWorkerPosition(transform.position)

                //TODO Figure out what to do if this workshop is null?
                if(closestWorkshop != null) {
                    workerUnit.workerBuilding = closestWorkshop
                    Mappers.workforce.get(closestWorkshop).workersAvailable.add(entity)
                }
            })
        }

        if(definition.isBuyer){
            val buyerComponent = BuyerComponent()

            init.initFuncs.add({
                val bc = Mappers.behaviour[entity] //We assume that the buyer has this since it needs behaviours
                bc.currTask = Tasks.buyItemDemandAndLeaveMap(bc.blackBoard)
            })

            entity.add(buyerComponent)
        }

        definition.compsToAdd.forEach { compDef ->
            val comp = Class.forName(compDef.compName).newInstance()

            System.out.println("[Factory] Class type: ${comp.javaClass.name}")

            compDef.fields.forEach { fieldData ->
                val field = comp.javaClass.getDeclaredField(fieldData[0])
                val fieldType = field.type
                val value = fieldData[1]
                field.isAccessible = true
                field.set(comp, Util.toObject(fieldType, value))
            }

            entity.add(comp as Component)
        }

        compsToAdd.forEach { entity.add(it) }

        entity.components.forEach { comp -> (comp as MyComponent).initialize() }
        MyGame.entityEngine.addEntity(entity)

        return entity
    }

    fun destroyEntity(entity:Entity){
        val comps = entity.components
        comps.forEach { comp -> (comp as MyComponent).dispose(entity)}
        MyGame.entityEngine.removeEntity(entity)
    }

    fun destroyEntityFamily(family: Family){
        val entities = MyGame.entityEngine.getEntitiesFor(family)
        entities.forEach { ent -> destroyEntity(ent) }
    }

    fun destroyAllEntities(){
        val entities = MyGame.entityEngine.getEntitiesFor(Family.all().get())
        entities.forEach { ent -> destroyEntity(ent) }
    }

}