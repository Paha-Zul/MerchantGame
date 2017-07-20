package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

class CheckInventoryNotEmpty(bb: BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()

        val empty = Mappers.inventory[bb.myself].isEmpty
        if(empty)
            controller.finishWithFailure()
        else
            controller.finishWithSuccess()
    }
}