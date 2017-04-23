package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.components.SellingItemsComponent


/**
 * Created by Paha on 1/16/2017.
 */
object Util {
    fun createPixel(color: Color): Texture {
        return createPixel(color, 1, 1)
    }

    fun createPixel(color: Color, width: Int, height: Int): Texture {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(color.r, color.g, color.b, color.a)
        pixmap.fillRectangle(0, 0, width, height)
        val pixmaptex = Texture(pixmap)
        pixmap.dispose()

        return pixmaptex
    }

    fun getClosestWorkshop(position:Vector2) : Entity?{
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

    fun getClosestBuildingType(position:Vector2, buildingType:BuildingComponent.BuildingType):Entity?{
        var closestDst = Float.MAX_VALUE
        var closest: Entity? = null

        Families.buildings.forEach { ent ->
            val bc = Mappers.building.get(ent)

            if (bc.buildingType == buildingType) {
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
     * Attempts to find the closest building of a type with an item in its inventory
     * @param position The position to search from
     * @param buildingType The type of building to do the search on
     * @param itemName The name of the item to find
     * @param itemAmount The amount of the item
     * @param buildingsToExclude A HashSet of buildings to exclude from the search
     * @return The closest building that was found, null if no building was found.
     */
    fun getClosestBuildingTypeWithItem(position:Vector2, buildingType:BuildingComponent.BuildingType, itemName:String, itemAmount:Int = 1, buildingsToExclude:HashSet<Entity> = hashSetOf()):Entity?{
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
    fun getClosestBuildingWithItemInInventory(position:Vector2, itemName:String, itemAmount:Int = 1, buildingsToExclude:HashSet<Entity>):Entity?{
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
    fun getClosestBuildingWithOutputItemInInventory(position:Vector2, itemName:String, itemAmount:Int = 1, buildingsToExclude:HashSet<Entity>):Entity?{
        var closestDst = Float.MAX_VALUE
        var closest: Entity? = null

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

    fun getClosestStockpileWithItem(position:Vector2, itemName:String, itemAmount:Int = 1) : Entity?{
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

    fun getClosestBuildingWithWorkerPosition(position:Vector2) : Entity?{
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

    fun getClosestSellingItem(position:Vector2, itemName:String, mustBeBuilding:Boolean = true, entitiesToIgnore:HashSet<Entity> = hashSetOf()) : Entity?{
        var closestDst = Float.MAX_VALUE
        var closest: Entity? = null

        Families.sellingItems.forEach { ent ->
            //If we aren't ignore this entity...
            if(!entitiesToIgnore.contains(ent)) {
                val sc = Mappers.selling.get(ent)
                val bc = Mappers.building.get(ent)
                val inv = Mappers.inventory.get(ent)

                val buildingCheck = !mustBeBuilding || (mustBeBuilding && bc != null)
                val hasItem = inv != null && inv.hasItem(itemName)
                var contains = false
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

    fun getRandomBuildingSellingItems():Entity?{
        val buildingsThatAreSellingItems = Families.buildingsSellingItems
        return buildingsThatAreSellingItems[MathUtils.random(buildingsThatAreSellingItems.size() - 1)]
    }

    fun roundUp(a:Float, increment:Int):Int{
        return (Math.ceil(a.toDouble()/increment)*increment).toInt()
    }

    fun roundDown(a:Float, increment:Int):Int{
        return (Math.floor(a.toDouble()/increment)*increment).toInt()
    }

    fun createBody(bodyType: BodyDef.BodyType, dimensions:Vector2, initialPosition:Vector2, fixtureData:Any, isSensor:Boolean = false): Body {
        val bodyDef = BodyDef()
        bodyDef.type = bodyType
        bodyDef.position.set(initialPosition)
        val body = MyGame.world.createBody(bodyDef)

        val fixtureDef = FixtureDef()
        val boxShape = PolygonShape()
        boxShape.setAsBox(dimensions.x*0.5f, dimensions.y*0.5f)

        fixtureDef.shape = boxShape
        fixtureDef.isSensor = isSensor

        val fixture = body.createFixture(fixtureDef)
        fixture.userData = fixtureData

        boxShape.dispose()

        return body
    }

    fun createBody(bodyType: String, dimensions:Vector2, initialPosition:Vector2, fixtureData:Any, isSensor:Boolean = false): Body {
        val _bodyType:BodyDef.BodyType
        when(bodyType){
            "dynamic" -> _bodyType = BodyDef.BodyType.DynamicBody
            else -> _bodyType = BodyDef.BodyType.StaticBody
        }

        return createBody(_bodyType, dimensions, initialPosition, fixtureData, isSensor)
    }

    fun getBuildingType(type:String):BuildingComponent.BuildingType{
        when(type.toLowerCase()){
            "wall" -> return BuildingComponent.BuildingType.Wall
            "shop" -> return BuildingComponent.BuildingType.Shop
            "workshop" -> return BuildingComponent.BuildingType.Workshop
            "stockpile" -> return BuildingComponent.BuildingType.Stockpile
            "house" -> return BuildingComponent.BuildingType.House
            else -> return BuildingComponent.BuildingType.None
        }
    }

    fun drawLineTo(start: Vector2, end:Vector2, pixel:TextureRegion, size:Float, batch: Batch){
        val rotation = MathUtils.atan2(end.y - start.y, end.x - start.x)* MathUtils.radiansToDegrees
        val distance = start.dst(end)
        pixel.setRegion(0f, 0f, distance/ size, 1f)
        batch.draw(pixel, start.x, start.y, 0f, 0f, distance, size, 1f, 1f, rotation)
    }

    fun drawLineTo(start: Vector2, end:Vector2, pixel: TextureRegionDrawable, size:Float, batch: Batch){
        val rotation = MathUtils.atan2(end.y - start.y, end.x - start.x)* MathUtils.radiansToDegrees
        val distance = start.dst(end)
        pixel.draw(batch, start.x, start.y, 0f, 0f, distance, size, 1f, 1f, rotation)
    }

    fun <T> toObject(clazz: Class<*>, value: String): T {
        if (Boolean::class.java == clazz) return java.lang.Boolean.parseBoolean(value) as T
        if (Byte::class.java == clazz) return java.lang.Byte.parseByte(value) as T
        if (Short::class.java == clazz) return java.lang.Short.parseShort(value) as T
        if (Int::class.java == clazz) return Integer.parseInt(value) as T
        if (Long::class.java == clazz) return java.lang.Long.parseLong(value) as T
        if (Float::class.java == clazz) return java.lang.Float.parseFloat(value) as T
        if (Double::class.java == clazz) return java.lang.Double.parseDouble(value) as T
        return value as T
    }

    /**
     * Removes an item from the reselling list of the selling component. If there was an item to remove, the linked entity that
     * was selling the item is given back the selling capabilities
     * @param sellingComp The SellingItemsComponent to modify
     * @param itemName The name of the item
     * @param itemPrice The price of the item (for further matching)
     */
    fun removeSellingItemFromReseller(sellingComp:SellingItemsComponent, itemName:String, itemPrice:Int){
        sellingComp.currSellingItems.removeAll { it.itemName == itemName } //Remove all currently selling items with this name

        sellingComp.resellingEntityItemLinks.forEach { (entityLink, itemPriceLinkList) -> //For each entity link...
            itemPriceLinkList.removeAll { it.itemName == itemName } //Remove all items in the linked item list
            val otherSelling = Mappers.selling[entityLink] //Get the selling component of the linked Entity
            val baseSellingItem = otherSelling.baseSellingItems.first {it.itemName == itemName} //Get the base selling item
            if(!otherSelling.currSellingItems.any { it.itemName == itemName }) //If ther linked Entity is not already currently selling it
                otherSelling.currSellingItems.add(baseSellingItem.copy()) //Add it back into the current selling list
        }
    }
}