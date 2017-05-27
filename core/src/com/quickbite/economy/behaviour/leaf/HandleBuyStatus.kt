package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuyerComponent
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 3/23/2017.
 */
class HandleBuyStatus(bb:BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()

        val buyer = Mappers.buyer[bb.myself]
        when(buyer.buyerFlag){

            //If we bought an item, deal with it!
            BuyerComponent.BuyerFlag.Bought -> {
                buyer.buyHistory.forEach { (itemName, itemAmount) ->
                    val itemDef = DefinitionManager.itemDefMap[itemName]!!
                    //TODO Maybe don't use a magic number?
                    //We multiply by 2 as a bonus...
                    buyer.needsSatisfactionRating += itemDef.need*2
                    buyer.luxurySatisfactionRating += itemDef.luxury*2
                }

                //TODO Maybe have a separate buy history and recent buy history?
                buyer.buyHistory.clear()
            }
            //If we are handling this after buying and it's not Bought, then we failed...
            else -> {
                bb.entitiesToIgnore.add(bb.targetEntity!!)
            }
        }

        buyer.buyerFlag = BuyerComponent.BuyerFlag.None

        this.controller.finishWithSuccess()
    }
}