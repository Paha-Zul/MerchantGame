package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 5/24/2017.
 *
 * Gets a 'spot' from an Entity using the spotType variable. The resulting spot (offset + entity position) is
 * assigned to bb.targetPosition
 */
class GetSpotOfEntity(bb:BlackBoard, val spotType:String) : LeafTask(bb){
    override fun start() {
        super.start()

        val tc = Mappers.transform.get(bb.targetEntity)
        val offset = Vector2(tc.spotMap[spotType]!![0])
        val pos = Mappers.transform.get(bb.targetEntity).position

        bb.targetPosition = Vector2(pos.x + offset.x, pos.y + offset.y)
        controller.finishWithSuccess()
    }
}