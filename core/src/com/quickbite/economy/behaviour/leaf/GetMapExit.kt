package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask

/**
 * Created by Paha on 1/17/2017.
 */
class GetMapExit(bb:BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()

        bb.targetPosition = Vector2(-1500f, 0f)
        controller.finishWithSuccess()
    }
}