package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuyerComponent
import com.quickbite.economy.util.ItemPriceLink
import com.quickbite.economy.util.ItemSold
import com.quickbite.economy.util.Mappers
import com.quickbite.spaceslingshot.util.EventSystem

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

        val unitInQueue = Mappers.building.get(bb.targetEntity).unitQueue.removeLast()
        val buyer = Mappers.buyer.get(unitInQueue)
        val buyerInv = Mappers.inventory.get(unitInQueue)

        buyer.buyerFlag = BuyerComponent.BuyerFlag.Failed //Initially set this to failed. If it doesn't set itself to Bought below then nothing was sold.

        for(i in (buyer.buyList.size - 1).downTo(0)){
            val pair = buyer.buyList[i]
            val list = sellComp.sellingItems.filter { it.itemName == pair.itemName } //Find if the building is selling the item
            val itemBeingSold:ItemPriceLink? = if(list.isEmpty()) null else list[0] //Get either the first index or assign null

            //If we are selling the item and out inventory contains it, let's sell!
            if(itemBeingSold != null && sellInv.hasItem(pair.itemName)){
                val itemAmtRemoved = sellInv.removeItem(pair.itemName, pair.itemAmount) //Remove the amount from seller's inventory
                buyerInv.addItem(pair.itemName, itemAmtRemoved) //Add the amount removed to the buyer's inventory
                pair.itemAmount -= itemAmtRemoved //Remove the amount we bought from the buyer's demands

                //Remove the money from the buyer's inventory
                val moneyRemoved = buyerInv.removeItem("Gold", itemBeingSold.itemPrice*itemAmtRemoved)

                //TODO Make sure this tax is okay for low value items. We don't want to be getting 1 gold tax on a 2 gold item
                val tax = if(moneyRemoved >=1) Math.max(1, (moneyRemoved*0.07f).toInt()) else 0 //We need at least 1 gold tax (if we made at least 1 gold)
                val taxedAmount = moneyRemoved - tax
                sellInv.addItem("Gold", taxedAmount)


                //If the buyer's demand for the item is 0 or less, remove it from the demands
                if(pair.itemAmount <= 0){
                    buyer.buyList.removeValue(pair, true)
                }

                val ic = Mappers.identity.get(unitInQueue)
                sellComp.sellHistory.add(ItemSold(pair.itemName, itemAmtRemoved, 10, 1.toFloat(), ic.name))

                EventSystem.callEvent("guiUpdateSellHistory", listOf()) //Call the event to update the gui if needed
                EventSystem.callEvent("addPlayerMoney", listOf(tax)) //Call the event to add money to the player

                buyer.buyerFlag = BuyerComponent.BuyerFlag.Bought //Set the buyer's flag as something was bought
                controller.finishWithSuccess() //Success!

                if(buyer.buyList.size > 0)
                    buyer.buyingIndex  = (buyer.buyingIndex + 1) % buyer.buyList.size //Increment the index for next time

                return
            }
        }

        val ic = Mappers.identity.get(unitInQueue)
        sellComp.sellHistory.add(ItemSold("nothing", 0, 10, 1.toFloat(), ic.name))
        EventSystem.callEvent("guiUpdateSellHistory", listOf()) //Call the event to update the gui if needed

        controller.finishWithFailure()

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