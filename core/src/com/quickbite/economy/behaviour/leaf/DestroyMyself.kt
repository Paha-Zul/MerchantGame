package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.MyGame
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask

/**
 * Created by Paha on 1/18/2017.
 */
class DestroyMyself(bb:BlackBoard) : LeafTask(bb){

    override fun start() {
        super.start()

        MyGame.entityEngine.removeEntity(bb.myself)
        controller.FinishWithSuccess()
    }
}