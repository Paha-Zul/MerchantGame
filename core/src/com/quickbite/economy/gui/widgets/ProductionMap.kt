package com.quickbite.economy.gui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.ProduceItemComponent
import com.quickbite.economy.managers.DefinitionManager

class ProductionMap(val productionComp:ProduceItemComponent) : Actor() {
    private val dot = MyGame.manager["dot", Texture::class.java]
    private val arrow = MyGame.manager["arrow", Texture::class.java]
    private val wheat = MyGame.manager["wheat_icon", Texture::class.java]

    private val dots:MutableList<Array<Dot>> = mutableListOf()
    private val inputs:Array<Icon>
    private val output:Icon

    private val labels:MutableList<LabelPosition> = mutableListOf()

    private val inputOutputIconSize = 50f
    private val dotSize = 4f
    private val arrowSize = 8f

    val xNodeSpace = 148f //Distance between each layer of nodes. Must be a multiple of dotSize
    val yNodeSpace = 64f //Must be a multiple of dot size

    init{
        inputs = Array(productionComp.productionList[0].requirements.size, {i ->
            val productionReq = productionComp.productionList[0].requirements[i]
            val item = DefinitionManager.itemDefMap[productionReq.itemName]!!
            val iconPosition = Vector2(0f, i*yNodeSpace)

            val label = Label("x${productionReq.itemAmount}", Label.LabelStyle(MyGame.defaultFont14, Color.WHITE))
            label.setPosition(iconPosition.x + inputOutputIconSize, iconPosition.y)
            labels += LabelPosition(label, Vector2(iconPosition.x + inputOutputIconSize, iconPosition.y))

            Icon(iconPosition, MyGame.manager[item.iconName, Texture::class.java])
        })

        val middle = ((inputs.size-1)*yNodeSpace)/2f
        val outputPosition = Vector2(xNodeSpace, middle)
        val outputItem = productionComp.productionList[0]

        output = Icon(outputPosition, MyGame.manager[DefinitionManager.itemDefMap[outputItem.produceItemName]!!.iconName, Texture::class.java])

        val label = Label("x${outputItem.produceAmount}", Label.LabelStyle(MyGame.defaultFont14, Color.WHITE))
        label.setPosition(outputPosition.x + inputOutputIconSize, outputPosition.y)
        labels += LabelPosition(label, Vector2(outputPosition.x + inputOutputIconSize, outputPosition.y))

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
            val size = if(spot.end) arrowSize else dotSize
            batch.draw(texture, x + spot.position.x, y + spot.position.y - size/2f, 0f, 0f, size, size, 1f, 1f,
                    spot.rotation, 0, 0, texture.width, texture.height, false, false)
        } }

        labels.forEach { it.label.setPosition(x + it.position.x - it.label.width/2f, y + it.position.y - it.label.height/2f) }
        labels.forEach { it.label.draw(batch, 1f) }
    }

    override fun act(delta: Float) {
        super.act(delta)

    }

    private fun calculateDots(firstNodePos:Vector2, secondNodePos:Vector2) : Array<Dot>{
        val disToMidY = firstNodePos.y - secondNodePos.y

        val numDotsX = ((secondNodePos.x - firstNodePos.x)/ dotSize).toInt()
        val numDotsY = Math.abs(disToMidY/ dotSize)
        val numDots = (numDotsX + numDotsY).toInt()

        val dir = if(firstNodePos.y < secondNodePos.y) 1 else -1

        val dots = Array(numDots, { i ->
            //We generate all X dots first here
            if(i<numDotsX){
                val c = if(i < numDotsX/2f) 0 else 1 //This is simpler than clamping it to 0 or 1. This is basically a flag
                val offsetY = c*(numDotsY* dotSize)*dir //Since we generate all X dots together, this is the offset if it should line up with the input or output icon
                Dot(Vector2(firstNodePos.x + i* dotSize, firstNodePos.y + offsetY), 0f, i == numDotsX-1 || i == 0)

            //Then we generate the Y dots
            }else{
                val i = i - numDotsX
                Dot(Vector2(firstNodePos.x + (numDotsX* dotSize)/2f, firstNodePos.y + i* dotSize *dir), 0f)
            }
        })

        return dots
    }

    private class Dot(val position:Vector2, val rotation:Float, val end:Boolean = false)
    private class Icon(val position:Vector2, val texture:Texture)
    private class LabelPosition(val label:Label, val position:Vector2)
}