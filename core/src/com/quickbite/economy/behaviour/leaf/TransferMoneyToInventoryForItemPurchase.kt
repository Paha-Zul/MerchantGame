package com.quickbite.economy.behaviour.leaf

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.util.objects.SellingItemData
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
    private var myWorkerBuilding: Entity? = null

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

        //If this is true we transfer all gold that we have
        if(giveAllMoneyRegardless){
            val money = fromInventory.removeItem("gold", -1)
            toInventory.addItem("gold", money)

            controller.finishWithSuccess()
            return
        }

        //This means that the other building is capable of selling stuff (not a stockpile) and will have
        //a price for the item. This condition doesn't affect our success.
        if(targetBuildingSelling != null){
            //Try to get from currSellingItems...
            var itemFromSelling = targetBuildingSelling.currSellingItems.firstOrNull { it.itemName == bb.targetItem.itemName }

            //If the item is null, try to get from baseSellingItems...
            if(itemFromSelling == null)
                itemFromSelling = targetBuildingSelling.baseSellingItems.firstOrNull { it.itemName == bb.targetItem.itemName }

            //As a last test if it's still null, check the output. We can grab from buildings that are exporting it
            if(itemFromSelling == null){
                val item = DefinitionManager.itemDefMap[bb.targetItem.itemName]!!
                val targetBuildingInv = Mappers.inventory[bb.targetEntity]
                val outputItem = targetBuildingInv.outputItems[bb.targetItem.itemName.toLowerCase()]
                //Check that the output is either 'all' or we have the output and are exporting it
                if(targetBuildingInv.outputItems.contains("all") || (outputItem != null && outputItem.exportable))
                    //Make a temp SellingItemData object to use here. Only the first 2 paramaters should matter for its purpose
                    //TODO Watch this. Maybe more sophisticated? Probably should be removed actually
                    itemFromSelling = SellingItemData(item.itemName, item.baseMarketPrice, -1, SellingItemData.ItemSource.Myself, null)
            }

            if(itemFromSelling == null){
                println("Error with: ${bb.targetItem}")
            }

            //TODO Problem here with itemFromSelling being null?
            val moneyInInventory = fromInventory.getItemAmount("gold")
            val amountCanBuy = Math.min(moneyInInventory/itemFromSelling!!.itemPrice, bb.targetItem.itemAmount)
            val moneyNeeded = amountCanBuy*itemFromSelling.itemPrice

            //Try to get the money from the 'fromInventory'. If we don't have enough, fail
            if(amountCanBuy < 1){
                controller.finishWithFailure()
                return
            }

            //Take the money out and give it...
            val moneyTaken = fromInventory.removeItem("gold", moneyNeeded)
            val moneyGiven = toInventory.addItem("gold", moneyTaken)
        }

        controller.finishWithSuccess()
    }
}