package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/22/2017.
 */
class SetMyWorkBuildingAsTarget(bb:BlackBoard) : LeafTask(bb){
    override fun start() {

        bb.targetEntity = Mappers.worker.get(bb.myself).workerBuilding
        controller.finishWithSuccess()
    }
}