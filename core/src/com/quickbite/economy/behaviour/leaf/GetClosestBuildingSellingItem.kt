package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/19/2017.
 */
class GetClosestBuildingSellingItem(bb:BlackBoard, val itemName:String = "") : LeafTask(bb){

    override fun start() {
        super.start()

        val building = Util.getClosestSellingItem(Mappers.transform.get(bb.myself).position, itemName)
        if(building!= null){
            bb.targetPosition = Vector2(Mappers.transform.get(building).position)
            bb.targetEntity = building
            bb.targetBuilding = Mappers.building.get(building)

            controller.FinishWithSuccess()
        }else
            controller.FinishWithFailure()
    }
}