package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuyerComponent
import com.quickbite.economy.util.ItemAmountLink
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
            val itemToBuy = buyer.buyList[i]
            val list = sellComp.currSellingItems.filter { it.itemName == itemToBuy.itemName } //Find if the building is selling the item
            val itemBeingSold:ItemPriceLink? = if(list.isEmpty()) null else list[0] //Get either the first index or assign null

            //If we are selling the item and out inventory contains it, let's sell!
            if(itemBeingSold != null && sellInv.hasItem(itemToBuy.itemName)){
                val itemAmtRemoved = sellInv.removeItem(itemToBuy.itemName, itemToBuy.itemAmount) //Remove the amount from seller's inventory
                buyerInv.addItem(itemToBuy.itemName, itemAmtRemoved) //Add the amount removed to the buyer's inventory
                itemToBuy.itemAmount -= itemAmtRemoved //Remove the amount we bought from the buyer's demands

                //Remove the money from the buyer's inventory
                val moneyRemovedFromBuyerInv = buyerInv.removeItem("Gold", itemBeingSold.itemPrice*itemAmtRemoved)

                //TODO Make sure this tax is okay for low value items. We don't want to be getting 1 gold tax on a 2 gold item
                val tax = if(moneyRemovedFromBuyerInv >=1) Math.max(1, (moneyRemovedFromBuyerInv*sellComp.taxRate).toInt()) else 0 //We need at least 1 gold tax (if we made at least 1 gold)
                var remainingMoney = moneyRemovedFromBuyerInv - tax

                sellInv.addItem("Gold", remainingMoney)

                //If the buyer's demand for the item is 0 or less, remove it from the demands
                if(itemToBuy.itemAmount <= 0){
                    buyer.buyList.removeValue(itemToBuy, true)
                }

                //Add the sell history
                val ic = Mappers.identity.get(unitInQueue)
                sellComp.sellHistory.add(ItemSold(itemToBuy.itemName, itemAmtRemoved, itemBeingSold.itemPrice, 1f, ic.name))

                //Call some events
                EventSystem.callEvent("guiUpdateSellHistory", listOf()) //Call the event to update the gui if needed
                EventSystem.callEvent("addPlayerMoney", listOf(tax)) //Call the event to add money to the player

                //Set the buyer flag
                buyer.buyerFlag = BuyerComponent.BuyerFlag.Bought //Set the buyer's flag as something was bought
                buyer.buyHistory.add(ItemAmountLink(itemToBuy.itemName, itemAmtRemoved)) //Add to the buyer history

                if(buyer.buyList.size > 0)
                    buyer.buyingIndex  = (buyer.buyingIndex + 1) % buyer.buyList.size //Increment the index for next time

                controller.finishWithSuccess() //Success!

                return
            }
        }

        val ic = Mappers.identity.get(unitInQueue)
        sellComp.sellHistory.add(ItemSold("nothing", 0, 10, 1f, ic.name))
        EventSystem.callEvent("guiUpdateSellHistory", listOf()) //Call the event to update the gui if needed

        controller.finishWithFailure()

//        buyer.buyList.forEach { pair ->
//            val selling = sellComp.currSellingItems.contains(pair.first)
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