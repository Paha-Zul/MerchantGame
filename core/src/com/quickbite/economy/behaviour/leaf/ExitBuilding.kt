package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 5/29/2017.
 *
 * Leaves a building, which involves showing the sprite and setting bb.insideEntity to null
 */
class ExitBuilding(bb: BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()

        bb.insideEntity = null
        Mappers.graphic.get(bb.myself).hide = false

        controller.finishWithSuccess()
    }
}