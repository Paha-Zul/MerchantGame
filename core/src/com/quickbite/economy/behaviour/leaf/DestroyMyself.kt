package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Factory

/**
 * Created by Paha on 1/18/2017.
 */
class DestroyMyself(bb:BlackBoard) : LeafTask(bb){

    override fun start() {
        super.start()

        Factory.destroyEntity(bb.myself)
        controller.finishWithSuccess()
    }
}