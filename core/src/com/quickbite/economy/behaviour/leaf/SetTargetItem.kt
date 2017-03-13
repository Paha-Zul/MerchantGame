package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask

/**
 * Created by Paha on 3/12/2017.
 *
 * Sets the bb.targetItem as the incoming itemName and itemAmount
 */
class SetTargetItem(bb:BlackBoard, val itemName:String, val itemAmount:Int = 1) : LeafTask(bb){
    override fun start() {
        bb.targetItem.itemName = itemName
        bb.targetItem.itemAmount = itemAmount

        controller.finishWithSuccess()
    }
}