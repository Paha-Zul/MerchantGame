package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 5/24/2017.
 */
class ReleaseResourceHarvesterSpot(bb:BlackBoard) : LeafTask(bb){
    override fun start() {
        super.start()
        val rc = Mappers.resource[bb.targetEntity]

        if(rc != null){
            rc.numCurrentHarvesters--
            controller.finishWithSuccess()
        }else
            controller.finishWithFailure()
    }
}