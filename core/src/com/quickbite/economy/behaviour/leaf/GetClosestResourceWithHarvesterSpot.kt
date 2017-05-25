package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.FindEntityUtil
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 5/24/2017.
 *
 * Gets the closest resource that has an open harvester spot and sets it as the bb.targetEntity. Also reserves a spot
 * on the resource
 */
class GetClosestResourceWithHarvesterSpot(bb:BlackBoard) : LeafTask(bb){
    override fun start() {
        super.start()

        val closest = FindEntityUtil.getClosestOpenResource(Mappers.transform[bb.myself].position)
        if(closest != null){
            bb.targetEntity = closest
            Mappers.resource[closest].numCurrentHarvesters++
            controller.finishWithSuccess()
        }else
            controller.finishWithFailure()
    }
}