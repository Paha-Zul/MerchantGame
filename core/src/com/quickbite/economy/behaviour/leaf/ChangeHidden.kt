package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/17/2017.
 */
class ChangeHidden(bb:BlackBoard, val hide:Boolean = false) : LeafTask(bb){

    override fun start() {
        Mappers.graphic.get(bb.myself).hide = hide
        this.controller.FinishWithSuccess()
    }
}