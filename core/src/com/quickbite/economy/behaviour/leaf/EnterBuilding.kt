package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 5/29/2017.
 *
 * Enters a building, which involves hiding the sprite and setting bb.insideEntity to bb.targetEntity
 */
class EnterBuilding(bb: BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()

        bb.insideEntity = bb.targetEntity
        Mappers.graphic.get(bb.myself).hide = true

        controller.finishWithSuccess()
    }
}