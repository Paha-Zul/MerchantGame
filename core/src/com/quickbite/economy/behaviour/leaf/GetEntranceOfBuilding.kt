package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/17/2017.
 */
class GetEntranceOfBuilding(bb:BlackBoard) : LeafTask(bb) {

    override fun start() {
        super.start()

        val offset = Vector2(bb.targetBuilding!!.entranceSpotOffsets[0])
        val pos = Mappers.transform.get(bb.targetEntity).position

        bb.targetPosition = Vector2(pos.x + offset.x, pos.y + offset.y)
        controller.FinishWithSuccess()
    }
}