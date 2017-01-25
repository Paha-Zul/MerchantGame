package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/23/2017.
 * Transfers an item from one inventory to the other.
 * @param bb The BlackBoard instance to use
 * @param toTargetInventory If true, transfers from my (bb.myself) inventory to the target (bb.targetEntity) inventory. If false, transfers
 * from the target to my inventory
 * @param itemName The name of the item to transfer. If empty (""), uses bb.targetItem instead
 * @param itemAmount The amount of the item to transfer. Not used if itemName is empty ("")
 */
class TransferFromInventoryToInventory(bb:BlackBoard, val toTargetInventory:Boolean, val itemName:String = "", val itemAmount:Int = 1) : LeafTask(bb){
    override fun start() {
        super.start()

        val fromInv = if(toTargetInventory) Mappers.inventory.get(bb.myself) else Mappers.inventory.get(bb.targetEntity)
        val toInv = if(toTargetInventory) Mappers.inventory.get(bb.targetEntity) else Mappers.inventory.get(bb.myself)

        val amt = fromInv.removeItem(itemName, itemAmount)
        toInv.addItem(itemName, amt)

        controller.FinishWithSuccess()
    }
}