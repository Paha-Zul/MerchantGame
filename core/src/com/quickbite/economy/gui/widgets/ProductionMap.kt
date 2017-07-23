package com.quickbite.economy.gui.widgets

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.quickbite.economy.MyGame
import com.quickbite.economy.managers.DefinitionManager

class ProductionMap(val inputItems:Array<String>, val outputItem:String) : Actor() {
    private val dot = MyGame.manager["dot", Texture::class.java]
    private val arrow = MyGame.manager["arrow", Texture::class.java]
    private val wheat = MyGame.manager["wheat_icon", Texture::class.java]

    val sizeOfDot = 8f

    private val dots:MutableList<Array<Dot>> = mutableListOf()
    private val inputs:Array<Icon>
    private val output:Icon

    private val inputOutputIconSize = 32f

    val xNodeSpace = 100f //Distance between each layer of nodes
    val yNodeSpace = 50f

    init{
        inputs = Array(inputItems.size, {i ->
            val item = DefinitionManager.itemDefMap[inputItems[i]]!!
            Icon(Vector2(0f, i*yNodeSpace), MyGame.manager[item.iconName, Texture::class.java])
        })

        val middle = ((inputs.size-1)*yNodeSpace)/2f
        val outputPosition = Vector2(xNodeSpace, middle)

        output = Icon(outputPosition, MyGame.manager[DefinitionManager.itemDefMap[outputItem]!!.iconName, Texture::class.java])

        inputs.forEachIndexed { index, _ ->
            dots += calculateDots(Vector2(inputOutputIconSize, index*yNodeSpace + inputOutputIconSize/2f),
                    Vector2(outputPosition.x, outputPosition.y + inputOutputIconSize/2f))
        }

        setBounds(0f, 0f, xNodeSpace + inputOutputIconSize, inputs.size*yNodeSpace)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        inputs.forEachIndexed { index, input ->
            batch.draw(input.texture, x + input.position.x, y + input.position.y, inputOutputIconSize, inputOutputIconSize)
        }

        batch.draw(output.texture, x + output.position.x, y + output.position.y, inputOutputIconSize, inputOutputIconSize)

        dots.forEach { it.forEach { spot ->
            val texture = if(spot.end) arrow else dot
            batch.draw(texture, x + spot.position.x, y + spot.position.y - sizeOfDot/2f, 0f, 0f, sizeOfDot, sizeOfDot, 1f, 1f,
                    spot.rotation, 0, 0, texture.width, texture.height, false, false)
        } }
    }

    private fun calculateDots(firstNodePos:Vector2, secondNodePos:Vector2) : Array<Dot>{
        val disToMidY = firstNodePos.y - secondNodePos.y

        val numDotsX = ((secondNodePos.x - firstNodePos.x)/sizeOfDot).toInt()
        val numDotsY = Math.abs(disToMidY/sizeOfDot)
        val numDots = (numDotsX + numDotsY).toInt()

        val dir = if(firstNodePos.y < secondNodePos.y) 1 else -1

        val dots = Array(numDots, { i ->
            if(i<numDotsX){
                val c = i/(numDotsX/2)
                val offsetY = c*(numDotsY*sizeOfDot)*dir
                Dot(Vector2(firstNodePos.x + i*sizeOfDot, firstNodePos.y + offsetY), 0f, i == numDotsX-1)
            }else{
                val i = i - numDotsX
                Dot(Vector2(firstNodePos.x + (numDotsX*sizeOfDot)/2f, firstNodePos.y + i*sizeOfDot*dir), 0f)
            }
        })

        return dots
    }

    private class Dot(val position:Vector2, val rotation:Float, val end:Boolean = false)
    private class Icon(val position:Vector2, val texture:Texture)
}