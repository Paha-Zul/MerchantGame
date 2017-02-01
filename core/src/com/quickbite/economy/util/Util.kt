package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.BuildingComponent


/**
 * Created by Paha on 1/16/2017.
 */
object Util {
    val whitePixel = createPixel(Color.WHITE)

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

    fun getClosestBuildingTypeWithItem(position:Vector2, buildingType:BuildingComponent.BuildingType, itemName:String, itemAmount:Int = 1):Entity?{
        var closestDst = Float.MAX_VALUE
        var closest: Entity? = null

        Families.buildings.forEach { ent ->
            val bc = Mappers.building.get(ent)
            val inv = Mappers.inventory.get(ent)

            if(bc.buildingType == buildingType && inv != null && inv.getItemAmount(itemName) >= itemAmount) {
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

    fun getClosestSellingItem(position:Vector2, itemName:String, mustBeBuilding:Boolean = true) : Entity?{
        var closestDst = Float.MAX_VALUE
        var closest: Entity? = null

        Families.sellingItems.forEach { ent ->
            val sc = Mappers.selling.get(ent)
            val bc = Mappers.building.get(ent)
            val inv = Mappers.inventory.get(ent)

            val buildingCheck = !mustBeBuilding || (mustBeBuilding && bc != null)
            val hasItem = inv != null && inv.hasItem(itemName)

            if(buildingCheck && sc.sellingItems.contains(itemName) && hasItem) {
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
}