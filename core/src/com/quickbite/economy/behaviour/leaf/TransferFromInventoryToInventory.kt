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
class TransferFromInventoryToInventory(bb:BlackBoard, val toTargetInventory:Boolean, var itemName:String = "", var itemAmount:Int = 1) : LeafTask(bb){
    var allOfInventory = false

    override fun check(): Boolean {
        return bb.targetEntity != null && Mappers.inventory[bb.targetEntity] != null
    }

    /**
    * Transfers an item from one inventory to the other.
    * @param bb The BlackBoard instance to use
    * @param toTargetInventory If true, transfers from my (bb.myself) inventory to the target (bb.targetEntity) inventory. If false, transfers
    * from the target to my inventory
    * @param allOfInventory If true, the entire inventory will be transferred.
    */
    constructor(bb:BlackBoard, toTargetInventory: Boolean, allOfInventory:Boolean):this(bb, toTargetInventory, "", 0){
        this.allOfInventory = allOfInventory
    }

    override fun start() {
        super.start()

        //If we aren't transferring the whole inventory
        if(!this.allOfInventory) {
            if (itemName == "") {
                itemName = bb.targetItem.itemName
                itemAmount = bb.targetItem.itemAmount
            }

            val fromInv = if (toTargetInventory) Mappers.inventory.get(bb.myself) else Mappers.inventory.get(bb.targetEntity)
            val toInv = if (toTargetInventory) Mappers.inventory.get(bb.targetEntity) else Mappers.inventory.get(bb.myself)

            val amt = fromInv.removeItem(itemName, itemAmount)
            toInv.addItem(itemName, amt)

        //If we are transferring the whole inventory
        }else{
            val fromInv = if (toTargetInventory) Mappers.inventory.get(bb.myself) else Mappers.inventory.get(bb.targetEntity)
            val toInv = if (toTargetInventory) Mappers.inventory.get(bb.targetEntity) else Mappers.inventory.get(bb.myself)

            for(item in fromInv.itemMap.values.toList()){
                val amt = fromInv.removeItem(item.itemName, item.itemAmount)
                toInv.addItem(item.itemName, amt)
            }
        }

        controller.finishWithSuccess()
    }
}