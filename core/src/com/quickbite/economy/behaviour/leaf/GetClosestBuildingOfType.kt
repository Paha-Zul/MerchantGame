package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.util.FindEntityUtil
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 3/18/2017.
 */
class GetClosestBuildingOfType(bb:BlackBoard, val buildingType: BuildingComponent.BuildingType) : LeafTask(bb){
    override fun start() {
        super.start()

        val building = FindEntityUtil.getClosestBuildingType(Mappers.transform.get(bb.myself).position, buildingType)
        if(building!= null){
            bb.targetPosition = Vector2(Mappers.transform.get(building).position)
            bb.targetEntity = building
            bb.targetBuilding = Mappers.building.get(building)

            controller.finishWithSuccess()
        }else
            controller.finishWithFailure()
    }
}