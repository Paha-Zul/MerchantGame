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
import com.quickbite.economy.event.events.ItemAmountChangeEvent
import com.quickbite.economy.event.events.ItemSoldEvent
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.objects.FarmObject
import com.quickbite.economy.objects.SellingItemData

/**
 * Created by Paha on 12/13/2016.
 *
 * A Factory object for creating Entities easily. Also handles destruction of Entities.
 */
object Factory {
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

        transform.dimensions.set(definition.transformDef.physicalDimensions.x, definition.transformDef.physicalDimensions.y)
        transform.spotMap = definition.transformDef.spots
        transform.position.set(position)

        //If the grid is set to block on placing, add an init func to block parts of the grid
        grid.blockWhenPlaced = definition.gridDef.blockGridWhenPlaced
        if(grid.blockWhenPlaced){
            init.initFuncs.add {
                MyGame.grid.setBlocked(transform.position.x, transform.position.y,
                        transform.dimensions.x*0.5f, transform.dimensions.y*0.5f, definition.gridDef.gridSpotsToNotBlock)
            }
        }

        entity.add(identityComp)
        entity.add(graphicComp)
        entity.add(transform)
        entity.add(grid)
        entity.add(init)
        entity.add(debug)

        //Check for building definition

        if(definition.buildingDef != null){
            val building = BuildingComponent()
            val buildingType = Util.getBuildingType(definition.buildingDef!!.buildingType)
            building.buildingType = buildingType
            entity.add(building)
        }

        //Check for selling items definition
        if(definition.sellingItems != null){
            val selling = SellingItemsComponent()

            //Things like this have to be copied or else they are linked and can be modified!!!
            val sellingList = Array<SellingItemData>()
            definition.sellingItems!!.sellingList.forEach { (itemName) ->
                val itemName = itemName.toLowerCase()
                sellingList.add(SellingItemData(itemName, DefinitionManager.itemDefMap[itemName]!!.baseMarketPrice, -1, SellingItemData.ItemSource.None))
            }

            selling.baseSellingItems = sellingList //Use the base array here
            selling.currSellingItems = Array(sellingList) //Copy it for this oned
            selling.isReselling = definition.sellingItems!!.isReselling
            selling.taxRate = definition.sellingItems!!.taxRate

            //A game event to listen for. Only record our tax collected here...
            GameEventSystem.subscribe<ItemSoldEvent>({
                selling.taxCollectedDaily += it.taxCollected
                selling.taxCollectedTotal += it.taxCollected
            }, identityComp.uniqueID)

            //Add this inventory listener in this init funcs so we can make sure the inventory component actually exists
            init.initFuncs.add {
                val inventory = Mappers.inventory[entity]

                //Add a listener for ALL items to notify of an inventory change
                //We specifically have this under the seller component area because we only want to keep track of what
                //items are available for selling
                inventory.addInventoryListener("all", { name, amtChanged, _ ->
                    if(name != "Gold") //Make sure to exclude gold...
                        GameEventSystem.fire(ItemAmountChangeEvent(name, amtChanged)) //Fire this event globally
                })

                //A listener for when things are sold and gold changes. This is for the economy stats mainly
                inventory.addInventoryListener("Gold", { _, amtChanged, _ ->
                    selling.incomeDaily += amtChanged
                    selling.incomeTotal += amtChanged
                })
            }

            entity.add(selling)
        }

        //These are the optional components that a building can have
        if(definition.inventoryDef != null){
            val inv = InventoryComponent()
            //We have to add this into the init funcs because we want listeners above ^ to be triggered when adding items
            init.initFuncs.add {
                definition.inventoryDef!!.debugItemList.forEach { (itemName, itemAmount) ->
                    inv.addItem(itemName, itemAmount)
                }
            }
            entity.add(inv)
        }

        //Check for workforce definition
        if(definition.workforceDef != null){
            val workforce = WorkForceComponent()
            workforce.numWorkerSpots = definition.workforceDef!!.workforceMax
            workforce.workerTasksLimits = Array(definition.workforceDef!!.workerTasks) //Make a copy here just to be sure...
            workforce.workerTasksLimits.forEach { workforce.workerTaskMap.put(it.taskName, Array())} //Populate the hashmap
            entity.add(workforce)
        }

        //Check for behaviour definition
        if(definition.behaviourDef != null){
            val behaviour = BehaviourComponent(entity)
            entity.add(behaviour)
        }

        //Check for velocity component
        if(definition.velocityDef != null){
            val velocity = VelocityComponent()
            velocity.baseSpeed = definition.velocityDef!!.baseSpeed
            entity.add(velocity)
        }

        //Physics component
        if(definition.physicsDef != null){
            val bodyComp = BodyComponent()
            val dimensions = if(definition.physicsDef!!.bodyDimensions.isZero)
                                 Vector2(transform.dimensions.x, transform.dimensions.y)
                            else
                                Vector2(definition.physicsDef!!.bodyDimensions.x, definition.physicsDef!!.bodyDimensions.y)

            val position = if(definition.physicsDef!!.bodyAnchor.isZero)
                                Vector2(transform.position.x, transform.position.y)
                            else
                                Vector2((transform.position.x + definition.physicsDef!!.bodyAnchor.x*dimensions.x),
                                        (transform.position.y + definition.physicsDef!!.bodyAnchor.y*dimensions.y))

            //Scale these down to Box2D's system...
            dimensions.set(dimensions.x*Constants.BOX2D_SCALE, dimensions.y*Constants.BOX2D_SCALE)
            position.set(position.x*Constants.BOX2D_SCALE, position.y*Constants.BOX2D_SCALE)

            init.initFuncs.add { bodyComp.body = Util.createBody(definition.physicsDef!!.bodyType, dimensions, position, entity, true) }

            entity.add(bodyComp)
        }

        //The production component
        if(definition.productionDef != null){
            val producesItems = ProduceItemComponent()

            //Add each production from the definition file into the produces items component
            definition.productionDef!!.produces.forEach { itemName ->
                producesItems.productionList.add(DefinitionManager.productionMap[itemName])
            }

            //Add anything that's harvested
            producesItems.harvests = definition.productionDef!!.harvests.toList()

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
                        inventory.outputItems += production.produceItemName
                        production.requirements.forEach { (itemName) ->
                            inventory.inputItems += itemName
                        }
                    }
                }
            }
        }

        //The worker component
        if(definition.workerDef != null){
            val workerUnit = WorkerUnitComponent()
            workerUnit.dailyWage = MathUtils.random(10, 75)
            entity.add(workerUnit)
        }

        //The buyer component
        if(definition.buyerDef != null){
            val buyerComponent = BuyerComponent()

            init.initFuncs.add({
                val bc = Mappers.behaviour[entity] //We assume that the buyer has this since it needs behaviours
                bc.currTask = Tasks.buyItemDemandAndLeaveMap(bc.blackBoard)
            })

            entity.add(buyerComponent)
        }

        //The resource component
        if(definition.resourceDef != null){
            val resourceComp = ResourceComponent()
            val resourceDef = definition.resourceDef!!

            with(resourceComp){
                numHarvestersMax = resourceDef.numHarvestersMax
                harvestAmount = resourceDef.harvestAmount
                resourceAmount = resourceDef.resourceAmount
                currResourceAmount = resourceDef.resourceAmount
                resourceType = resourceDef.resourceType
                harvestItemName = resourceDef.harvestedItemName
                baseHarvestTime = resourceDef.baseHarvestTime
                canRegrow = resourceDef.canRegrow
                baseRegrowTime = resourceDef.baseRegrowTimeRange
                harvestedGraphicName = resourceDef.harvestedGraphicName
            }

            entity.add(resourceComp)
        }

        //The farm definition
        if(definition.farmDef != null){
            val farmComp = FarmComponent()
            val farmDef = definition.farmDef!!

            farmComp.plantSpots = kotlin.Array(definition.farmDef!!.cols, {x -> kotlin.Array(definition.farmDef!!.rows, {y ->
                val xPos = x*(farmDef.xSpace + 16) + farmDef.offset.x*graphicComp.sprite.width + MathUtils.random(-2, 2)
                val yPos = y*(farmDef.ySpace + 16) + farmDef.offset.y*graphicComp.sprite.height
                val texture:Texture = MyGame.manager["${farmComp.itemToGrow}_plant"]
                FarmObject(Vector2(xPos, yPos), 0f, Sprite(texture)).apply {
                    sprite.setSize(0f, 0f)
                    needsTending = true //A way of initially planting the seed
                }
            })})

            init.initFuncs.add {
                val inventory = Mappers.inventory[entity]
                //TODO In the future, we need to change the output items when the item to grow changes
                inventory.outputItems.add(farmComp.itemToGrow) //Initially put this as one of our output items
            }

            entity.add(farmComp)
        }

        //Add any additional components that were sent in as parameters
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
        MyGame.entityEngine.removeEntity(entity) //Remove it from the engine
        entity.removeAll() //We remove all components here because we use 0 components as a sign of being destroyed. IMPORTANT!!!
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