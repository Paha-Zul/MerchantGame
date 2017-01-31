package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/22/2017.
 */
class GetClosestBuildingWithItem(bb:BlackBoard, val buildingType:BuildingComponent.BuildingType, val itemName:String, val itemAmount:Int = 1) : LeafTask(bb) {
    override fun start() {
        bb.targetEntity = Util.getClosestBuildingTypeWithItem(Mappers.transform.get(bb.myself).position, buildingType, itemName, itemAmount)
        if(bb.targetEntity == null)
            controller.FinishWithFailure()
        else
            controller.FinishWithSuccess()
    }
}