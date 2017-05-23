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
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ItemSoldEvent
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.managers.BuildingDefManager
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.objects.Building
import com.quickbite.economy.objects.SellingItemData

/**
 * Created by Paha on 12/13/2016.
 *
 * A Factory object for creating Entities easily. Also handles destruction of Entities.
 */
object Factory {

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
        graphicComp.initialAnimation = definition.graphicDef.initialAnimation

        transform.dimensions.set(definition.physicalDimensions[0], definition.physicalDimensions[1])
        transform.position.set(position)

        grid.blockWhenPlaced = definition.gridBlockWhenPlaced

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
            val sellingList = Array<SellingItemData>()
            definition.sellingItems.sellingList.forEach { (itemName) ->
                sellingList.add(SellingItemData(itemName, DefinitionManager.itemDefMap[itemName]!!.baseMarketPrice, -1, SellingItemData.ItemSource.None))
            }

            selling.baseSellingItems = sellingList //Use the base array here
            selling.currSellingItems = Array(sellingList) //Copy it for this oned
            selling.isReselling = definition.sellingItems.isReselling
            selling.taxRate = definition.sellingItems.taxRate


            //A game event to listen for. Record our profit and tax collected from this event
            GameEventSystem.subscribe<ItemSoldEvent>({
                selling.incomeDaily += it.profit
                selling.taxCollectedDaily += it.taxCollected
            }, identityComp.uniqueID)

            //Add this inventory listener in this init funcs so we can make sure the inventory component actually exists
            init.initFuncs.add {
                val inventory = Mappers.inventory[entity]
                inventory.addInventoryListener("Gold", { _, amtChanged, _ ->
                    selling.incomeDaily += amtChanged
                })
            }

            entity.add(selling)
        }

        //Check for workforce definition
        if(definition.workforceDef.workforceMax > 0){
            val workforce = WorkForceComponent()
            workforce.numWorkerSpots = definition.workforceDef.workforceMax
            workforce.workerTasksLimits = Array(definition.workforceDef.workerTasks) //Make a copy here just to be sure...
            workforce.workerTasksLimits.forEach { workforce.workerTaskMap.put(it.taskName, Array())} //Populate the hashmap
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

        //Physics component
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
                producesItems.productionList.add(DefinitionManager.productionMap[itemName])
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
            workerUnit.dailyWage = MathUtils.random(10, 75)
            entity.add(workerUnit)
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
        val comps = entity.components //Get all components
        GameEventSystem.unsubscribeAll(Mappers.identity[entity].uniqueID) //Unsubscribe from all game events
        comps.forEach { comp -> (comp as MyComponent).dispose(entity)} //Dispose each component
        entity.removeAll() //We remove all components here because we use 0 components as a sign of being destroyed. IMPORTANT!!!
        MyGame.entityEngine.removeEntity(entity) //Remove it from the engine
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