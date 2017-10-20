package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 7/17/2017.
 */
class GetPlant(bb:BlackBoard, val type:String = "tend") : LeafTask(bb) {
    override fun start() {
        super.start()
        val targetTC = Mappers.transform[bb.targetEntity]

        if(type == "tend")
            bb.targetPlantSpot = Mappers.farm[bb.targetEntity].plantSpots.flatten().firstOrNull { it.needsTending && !it.reseved }
        else if(type == "harvest")
            bb.targetPlantSpot = Mappers.farm[bb.targetEntity].plantSpots.flatten().firstOrNull { it.readyToHarvest && !it.reseved}
        else if(type == "sow")
            bb.targetPlantSpot = Mappers.farm[bb.targetEntity].plantSpots.flatten().firstOrNull { it.plantName == ""}

        if(bb.targetPlantSpot == null){
            controller.finishWithFailure()
            return
        }

        bb.targetPosition = Vector2(bb.targetPlantSpot!!.position.x + targetTC.position.x, bb.targetPlantSpot!!.position.y + targetTC.position.y)
        bb.targetPlantSpot!!.reseved = true

        controller.finishWithSuccess()
    }
}