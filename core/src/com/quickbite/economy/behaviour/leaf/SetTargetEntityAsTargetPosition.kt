package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/22/2017.
 */
class SetTargetEntityAsTargetPosition(bb:BlackBoard) : LeafTask(bb){
    override fun start() {
        super.start()

        bb.targetPosition = Vector2(Mappers.transform.get(bb.targetEntity).position)
        controller.finishWithSuccess()
    }
}