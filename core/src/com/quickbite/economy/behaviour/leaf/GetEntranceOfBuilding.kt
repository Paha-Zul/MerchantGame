package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/17/2017.
 *
 * Sets the bb.targetPosition of the bb.targetEntity entrance spot
 */
class GetEntranceOfBuilding(bb:BlackBoard) : LeafTask(bb) {

    override fun check(): Boolean {
        return bb.targetEntity != null
    }

    override fun start() {
        super.start()

        val building = Mappers.building.get(bb.targetEntity)
        val offset = Vector2(building.entranceSpotOffsets[0])
        val pos = Mappers.transform.get(bb.targetEntity).position

        bb.targetPosition = Vector2(pos.x + offset.x, pos.y + offset.y)
        controller.finishWithSuccess()
    }
}