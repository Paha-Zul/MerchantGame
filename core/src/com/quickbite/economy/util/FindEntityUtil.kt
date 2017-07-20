package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.filters.ResourceParameter

/**
 * Created by Paha on 5/13/2017.
 */
object FindEntityUtil {
    fun getClosestWorkshop(position: Vector2) : Entity?{
        var closestDst = Float.MAX_VALUE
        var closest: Entity? = null

        Families.buildings.forEach { ent ->
            val bc = Mappers.building.get(ent)
            if(bc.buildingType == BuildingComponent.BuildingType.Workshop) {
                val tm = Mappers.transform.get(ent)
                val dst = tm.position.dst2(position)

                if (dst <= closestDst) {
                    closest = ent
                    closestDst = dst
                }
            }
        }

        return closest
    }

    fun getClosestBuildingType(position: Vector2, buildingType: BuildingComponent.BuildingType, predicate:(Entity) -> Boolean = {true}): Entity?{
        var closestDst = Float.MAX_VALUE
        var closest: Entity? = null

        Families.buildings.forEach { ent ->
            val bc = Mappers.building.get(ent)

            if (bc.buildingType == buildingType) {
                val tm = Mappers.transform.get(ent)
                val dst = tm.position.dst2(position)

                if (dst <= closestDst && predicate(ent)) {
                    closest = ent
                    closestDst = dst
                }
            }
        }

        return closest
    }

    /**
     * Attempts to find the closest building of a type with an item in its inventory
     * @param position The position to search from
     * @param buildingType The type of building to do the search on
     * @param itemName The name of the item to find
     * @param itemAmount The amount of the item
     * @param buildingsToExclude A HashSet of buildings to exclude from the search
     * @return The closest building that was found, null if no building was found.
     */
    fun getClosestBuildingTypeWithItemInInventory(position: Vector2, buildingType: BuildingComponent.BuildingType, itemName:String, itemAmount:Int = 1, buildingsToExclude:HashSet<Entity> = hashSetOf()): Entity?{
        var closestDst = Float.MAX_VALUE
        var closest: Entity? = null

        Families.buildings.forEach { ent ->
            if(!buildingsToExclude.contains(ent)) {

                val bc = Mappers.building.get(ent)
                val inv = Mappers.inventory.get(ent)

                if (bc.buildingType == buildingType && inv != null && inv.getItemAmount(itemName) >= itemAmount) {
                    val tm = Mappers.transform.get(ent)
                    val dst = tm.position.dst2(position)

                    if (dst <= closestDst) {
                        closest = ent
                        closestDst = dst
                    }
                }
            }
        }

        return closest
    }

    /**
     * Attempts to find the nearest building with an item in its inventory
     * @param position The position to search from
     * @param itemName The name of the item to get
     * @param itemAmount The amount of the item
     * @param buildingsToExclude A HashSet of buildings to exclude from the search
     * @return The closest building that was found, null if no building was found.
     */
    fun getClosestBuildingWithItemInInventory(position: Vector2, itemName:String, itemAmount:Int = 1, buildingsToExclude:HashSet<Entity>): Entity?{
        var closestDst = Float.MAX_VALUE
        var closest: Entity? = null

        Families.buildings.forEach { ent ->
            if(!buildingsToExclude.contains(ent)) {

                val bc = Mappers.building.get(ent)
                val inv = Mappers.inventory.get(ent)

                if (inv != null && inv.getItemAmount(itemName) >= itemAmount) {
                    val tm = Mappers.transform.get(ent)
                    val dst = tm.position.dst2(position)

                    if (dst <= closestDst) {
                        closest = ent
                        closestDst = dst
                    }
                }
            }
        }

        return closest
    }

    /**
     * Attempts to find the nearest building that has an item in it's output and inventory
     * @param position The position to search from
     * @param itemName The name of the item to get
     * @param itemAmount The amount of the item
     * @param buildingsToExclude A HashSet of buildings to exclude from the search
     * @return The closest building that was found, null if no building was found.
     */
    fun getClosestBuildingWithOutputItemInInventory(position: Vector2, itemName:String, itemAmount:Int = 1, buildingsToExclude:HashSet<Entity>): Entity?{
        var closestDst = Float.MAX_VALUE
        var closest: Entity? = null
        val itemName = itemName.toLowerCase()

        Families.buildings.forEach { ent ->
            if(!buildingsToExclude.contains(ent)) {

                val bc = Mappers.building.get(ent)
                val inv = Mappers.inventory.get(ent)
                val hasOutput = inv.outputItems.contains("All") || inv.outputItems.contains(itemName)

                if (inv != null && hasOutput && inv.getItemAmount(itemName) >= itemAmount) {
                    val tm = Mappers.transform.get(ent)
                    val dst = tm.position.dst2(position)

                    if (dst <= closestDst) {
                        closest = ent
                        closestDst = dst
                    }
                }
            }
        }

        return closest
    }

    /**
     * Gets the closest stockpile with an item in its inventory
     * @param position The position to get closest to
     * @param itemName The name of the item
     * @param itemAmount The minimum amount for the stockpile to have
     * @return The closest Entity if found, null otherwise.
     */
    fun getClosestStockpileWithItem(position: Vector2, itemName:String, itemAmount:Int = 1) : Entity?{
        var closestDst = Float.MAX_VALUE
        var closest: Entity? = null

        Families.buildings.forEach { ent ->
            val bc = Mappers.building.get(ent)
            val inv = Mappers.inventory.get(ent)
            if(bc.buildingType == BuildingComponent.BuildingType.Stockpile && inv != null && inv.getItemAmount(itemName) >= itemAmount) {
                val tm = Mappers.transform.get(ent)
                val dst = tm.position.dst2(position)

                if (dst <= closestDst) {
                    closest = ent
                    closestDst = dst
                }
            }
        }

        return closest
    }

    /**
     * Gets the closest building that has a WorkForce component and an open worker spot
     * @param position The position to get closest to.
     * @return The closest building if one was found, null otherwise
     */
    fun getClosestBuildingWithWorkerPosition(position: Vector2) : Entity?{
        var closestDst = Float.MAX_VALUE
        var closest: Entity? = null

        Families.buildings.forEach { ent ->
            val wc = Mappers.workforce.get(ent)
            if(wc != null && wc.workersAvailable.size < wc.numWorkerSpots) {
                val tm = Mappers.transform.get(ent)
                val dst = tm.position.dst2(position)

                if (dst <= closestDst) {
                    closest = ent
                    closestDst = dst
                }
            }
        }

        return closest
    }

    /**
     * Gets the closest Entity that is selling an item
     * @param position The position to get closest to
     * @param itemName The name of the item
     * @param mustBeBuilding If the Entity has to be a building or not
     * @param entitiesToIgnore A HashSet of Entities to ignore in the search for any reason
     * @return The closest Entity if one was found, null otherwise.
     */
    fun getClosestSellingItem(position: Vector2, itemName:String, mustBeBuilding:Boolean = true, entitiesToIgnore:HashSet<Entity> = hashSetOf()) : Entity?{
        var closestDst = Float.MAX_VALUE
        var closest: Entity? = null

        Families.sellingItems.forEach { ent ->
            //If we aren't ignore this entity...
            if(!entitiesToIgnore.contains(ent)) {
                val sc = Mappers.selling.get(ent)
                val bc = Mappers.building.get(ent)
                val inv = Mappers.inventory.get(ent)

                val buildingCheck = !mustBeBuilding || (mustBeBuilding && bc != null)
                val hasItem = inv != null && inv.hasItem(itemName) //If the entity has the item in its inventory
                var contains = false //If the entity contains the item in the selling list
                sc.currSellingItems.forEach contains@ {
                    if (it.itemName == itemName) {
                        contains = true; return@contains
                    }
                }

                if (buildingCheck && contains && hasItem) {
                    val tm = Mappers.transform.get(ent)
                    val dst = tm.position.dst2(position)

                    if (dst <= closestDst) {
                        closest = ent
                        closestDst = dst
                    }
                }
            }
        }

        return closest
    }

    fun getRandomBuildingSellingItems(): Entity?{
        val buildingsThatAreSellingItems = Families.buildingsSellingItems
        return buildingsThatAreSellingItems[MathUtils.random(buildingsThatAreSellingItems.size() - 1)]
    }

    fun getClosestOpenResource(position:Vector2, params:ResourceParameter):Entity?{
        var closest:Entity? = null
        var closestDst = Float.MAX_VALUE

        Families.resources.forEach { entity ->
            val tc = Mappers.transform[entity]
            val rc = Mappers.resource[entity]

            val isType = params.resourceType == "" || params.resourceType == rc.resourceType
            val isItem = rc.harvestItemName == "" || params.harvestedItemNames.contains(rc.harvestItemName)
            val isNotHarvested = !rc.harvested
            val isQuality = true

            val meetsReq = isType && isItem && isQuality && isNotHarvested

            //If we have no room for another harvester or the type doesn't match, continue...
            if(rc.numCurrentHarvesters >= rc.numHarvestersMax || !meetsReq)
                return@forEach

            val dst = tc.position.dst2(position)
            if(dst < closestDst){
                closest = entity
                closestDst = dst
            }
        }

        return closest
    }
}