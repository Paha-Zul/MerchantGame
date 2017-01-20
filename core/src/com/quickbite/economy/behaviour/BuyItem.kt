package com.quickbite.economy.behaviour

import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/19/2017.
 */
class BuyItem(bb:BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()

        val targetInv = Mappers.inventory.get(bb.targetEntity)
        if(targetInv == null){
            controller.FinishWithFailure()
            return
        }

        val myInv = Mappers.inventory.get(bb.myself)

        val amt = targetInv.removeItem("Wood Plank", 10)
        myInv.addItem("Wood Plank", amt)

        System.out.println("[BuyItem] Target inventory has ${targetInv.getItemAmount("Wood Plank")} planks, my inv has ${myInv.getItemAmount("Wood Plank")}")

        controller.FinishWithSuccess()
    }
}