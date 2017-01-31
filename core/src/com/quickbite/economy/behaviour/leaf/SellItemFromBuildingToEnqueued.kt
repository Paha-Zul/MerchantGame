package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuyerComponent
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/25/2017.
 *
 * Attempts to sell an item to the first queue person in the bb.targetEntity's building queue.
 */
class SellItemFromBuildingToEnqueued(bb:BlackBoard) : LeafTask(bb){

    override fun check(): Boolean {
        return Mappers.building.get(bb.targetEntity).unitQueue.size > 0
    }

    override fun start() {
        val sellComp = Mappers.selling.get(bb.targetEntity)
        val sellInv = Mappers.inventory.get(bb.targetEntity)

        val unit = Mappers.building.get(bb.targetEntity).unitQueue.removeLast()
        val buyer = Mappers.buyer.get(unit)
        val buyerInv = Mappers.inventory.get(unit)

        buyer.buyerFlag = BuyerComponent.BuyerFlag.Failed //Initially set this to failed. If it doesn't set itself to Bought below then nothing was sold.

        for(i in (buyer.buyList.size - 1).downTo(0)){
            val pair = buyer.buyList[i]
            val selling = sellComp.sellingItems.contains(pair.first)
            if(selling && sellInv.hasItem(pair.first)){
                val amt = sellInv.removeItem(pair.first, pair.second)
                buyerInv.addItem(pair.first, amt)
                pair.second -= amt
                if(pair.second <= 0){
                    buyer.buyList.removeValue(pair, true)
                }

                System.out.println("[SellItemFromBuildingToEnqueued] Something was sold!")
                buyer.buyerFlag = BuyerComponent.BuyerFlag.Bought
                controller.FinishWithSuccess()
                return
            }
        }

        controller.FinishWithFailure()

//        buyer.buyList.forEach { pair ->
//            val selling = sellComp.sellingItems.contains(pair.first)
//            if(selling && sellInv.hasItem(pair.first)){
//                val amt = sellInv.removeItem(pair.first, pair.second)
//                buyerInv.addItem(pair.first, amt)
//                pair.second -= amt
//                if(pair.second <= 0)
//
//            }
//        }
    }
}