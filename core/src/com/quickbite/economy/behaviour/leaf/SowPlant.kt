package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.MyGame
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.util.Mappers

class SowPlant(bb:BlackBoard) : LeafTask(bb) {
    var plantingTime = 0f
    var counter = 0f

    override fun start() {
        super.start()
        counter = 0f
        plantingTime = MathUtils.random()*3 //TODO Fix this to be not a magic number
    }

    override fun update(delta: Float) {
        super.update(delta)

        counter+=delta
        if(counter >= plantingTime){
            //Set the plant name and tending to false. Then finish
            bb.targetPlantSpot!!.plantName = Mappers.farm[bb.targetEntity].itemToGrow
            bb.targetPlantSpot!!.needsTending = false
            bb.targetPlantSpot!!.sprite.texture = MyGame.manager[DefinitionManager.plantDefMap[bb.targetPlantSpot!!.plantName]!!.graphicName]
            controller.finishWithSuccess()
        }
    }
}