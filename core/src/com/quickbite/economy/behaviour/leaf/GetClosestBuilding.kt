package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/17/2017.
 */
class GetClosestBuilding(bb:BlackBoard) :LeafTask(bb){
    override fun start() {
        val building = Util.getClosestWorkshop(Mappers.transform.get(bb.myself).position)
        if(building!= null){
            bb.targetPosition = Vector2(Mappers.transform.get(building).position)
            bb.targetEntity = building
            bb.targetBuilding = Mappers.building.get(building)

            controller.finishWithSuccess()
        }else
            controller.finishWithFailure()
    }
}