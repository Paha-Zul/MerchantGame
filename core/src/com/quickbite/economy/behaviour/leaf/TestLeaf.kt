package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask

/**
 * Created by Paha on 10/15/2016.
 */
class TestLeaf(bb: BlackBoard, val test:String = "Test") : LeafTask(bb) {
    override fun start() {
        System.out.println(test)
        controller.finishWithSuccess()
    }
}