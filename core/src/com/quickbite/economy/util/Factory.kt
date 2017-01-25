package com.quickbite.economy.util

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.MyGame
import com.quickbite.economy.objects.*

/**
 * Created by Paha on 12/13/2016.
 */
object Factory {

    fun createObject(type:String, position:Vector2, dimensions:Vector2, textureName:String, compsToAdd:List<Component> = listOf()):Entity? {
        var thing:Entity? = null

        //TODO Figure out how to use dimensions better. Right now they are hardcoded for prototyping
        when(type){
            "workshop" -> {
                dimensions.set(125f, 125f)
                val sprite = Sprite(MyGame.manager[textureName, Texture::class.java])
                sprite.setSize(dimensions.x, dimensions.y)

                thing = Workshop(sprite, position, dimensions)
            }

            "shop" -> {
                dimensions.set(75f, 75f)
                val sprite = Sprite(MyGame.manager[textureName, Texture::class.java])
                sprite.setSize(dimensions.x, dimensions.y)

                thing = Shop(sprite, position, dimensions)
            }

            "wall" -> {
                dimensions.set(25f, 25f)
                val sprite = Sprite(MyGame.manager[textureName, Texture::class.java])
                sprite.setSize(dimensions.x, dimensions.y)

                thing = Wall(sprite, position, dimensions)
            }

            "stockpile" -> {
                dimensions.set(90f, 90f)
                val sprite = Sprite(MyGame.manager[textureName, Texture::class.java])
                sprite.setSize(dimensions.x, dimensions.y)

                thing = Stockpile(sprite, position, dimensions)
            }

            "worker" -> {
                dimensions.set(20f, 20f)
                val sprite = Sprite(MyGame.manager[textureName, Texture::class.java])
                sprite.setSize(dimensions.x, dimensions.y)

                thing = WorkerUnit(sprite, position, dimensions)
            }
        }


        if(thing != null) {
            compsToAdd.forEach { thing!!.add(it) }
            MyGame.entityEngine.addEntity(thing)
        }

        return thing
    }


    fun createBuilding(type:String, position:Vector2, dimensions:Vector2, textureName:String):Entity? {
        var building:Entity? = null

        when(type){
            "workshop" -> {
                val sprite = Sprite(MyGame.manager[textureName, Texture::class.java])
                sprite.setSize(dimensions.x, dimensions.y)

                building = Workshop(sprite, position, dimensions)
            }
            "wall" -> {
                val sprite = Sprite(MyGame.manager[textureName, Texture::class.java])
                sprite.setSize(dimensions.x, dimensions.y)

                building = Wall(sprite, position, dimensions)
            }
        }

        if(building != null) {
            MyGame.entityEngine.addEntity(building)
        }

        return building
    }

    fun createFootUnit(type:String, position:Vector2, dimensions:Vector2, textureName:String):Entity? {
        var unit:Entity? = null

        when(type){
            "worker" -> {
                val sprite = Sprite(MyGame.manager[textureName, Texture::class.java])
                sprite.setSize(dimensions.x, dimensions.y)

                unit = WorkerUnit(sprite, position, dimensions)
            }
        }

        if(unit != null)
            MyGame.entityEngine.addEntity(unit)

        return unit
    }
}