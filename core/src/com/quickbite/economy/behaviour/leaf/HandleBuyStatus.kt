package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuyerComponent
import com.quickbite.economy.managers.ItemDefManager
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 3/23/2017.
 */
class HandleBuyStatus(bb:BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()

        val buyer = Mappers.buyer[bb.myself]
        when(buyer.buyerFlag){
            BuyerComponent.BuyerFlag.Bought -> {
                buyer.buyHistory.forEach { item ->
                    if(ItemDefManager.itemDefMap[item.itemName]!!.category == "Food")
                        //The buyer starts with necessity negative equal to the amount of food it needs
                        //We double this to provide a positive rating if at least half the food is bought
                        buyer.needsSatisfactionRating += item.itemAmount*2
                }
            }
        }

        buyer.buyerFlag = BuyerComponent.BuyerFlag.None

        this.controller.finishWithSuccess()
    }
}