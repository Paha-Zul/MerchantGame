package com.quickbite.economy.behaviour.leaf

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 4/2/2017.
 * This is used mainly for shops reselling items from workshops. The worker (when about to haul an item) will
 * look into its target entity that it will be hauling from, pull the price of the item it's hauling, and take
 * money from its worker building.
 *
 * @param bb The BlackBoard object
 * @param toMyself True if the money should go from the target building to myself. False if the money should go from myself
 * @param giveAllMoneyRegardless If true, all money is transfered from the 'fromInventory' to the 'toInventory'. Otherwise, works normally.
 * to the target building.
 */
class TransferMoneyToInventoryForItemPurchase(bb:BlackBoard, val toMyself:Boolean = true, val giveAllMoneyRegardless:Boolean = false) : LeafTask(bb){
    var myWorkerBuilding: Entity? = null

    override fun check(): Boolean {
        myWorkerBuilding = Mappers.worker[bb.myself].workerBuilding
        return bb.targetEntity != null && myWorkerBuilding != null && bb.targetItem.itemName != ""
    }

    override fun start() {
        super.start()

        val myBuildingInventory = Mappers.inventory[myWorkerBuilding!!]
        val myInventory = Mappers.inventory[bb.myself]
        val targetBuildingSelling = Mappers.selling[bb.targetEntity]
        val targetEntityInventory = Mappers.inventory[bb.targetEntity]

        val toInventory = if(toMyself) myInventory else targetEntityInventory //Either to me or to the target entity
        val fromInventory = if(toMyself) myBuildingInventory else myInventory //Either from my building or to my inventory

        if(giveAllMoneyRegardless){
            val money = fromInventory.removeItem("Gold", -1)
            toInventory.addItem("Gold", money)

            controller.finishWithSuccess()
            return
        }

        //This means that the other building is capable of selling stuff (not a stockpile) and will have
        //a price for the item. This condition doesn't affect our success.
        if(targetBuildingSelling != null){
            val itemPrice = targetBuildingSelling.baseSellingItems.first { it.itemName == bb.targetItem.itemName }.itemPrice
            val moneyInInventory = fromInventory.getItemAmount("Gold")
            val amountCanBuy = Math.min(moneyInInventory/itemPrice, bb.targetItem.itemAmount)
            val moneyNeeded = amountCanBuy*itemPrice

            //TODO Should probably try to do partial orders
            //Try to get the money from the 'fromInventory'. If we don't have enough, fail
            if(amountCanBuy < 1){
                controller.finishWithFailure()
                return
            }

            //Take the money out and give it...
            val moneyTaken = fromInventory.removeItem("Gold", moneyNeeded)
            val moneyGiven = toInventory.addItem("Gold", moneyTaken)
        }

        controller.finishWithSuccess()
    }
}