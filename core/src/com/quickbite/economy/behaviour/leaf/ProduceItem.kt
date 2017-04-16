package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/25/2017.
 *
 * Produces an item and adds it to the bb.targetEntity inventory
 * @param bb The BlackBoard instance
 * @param itemName The name of the item
 * @param itemAmount The amount of the item
 */
class ProduceItem(bb:BlackBoard, var itemName:String = "", var itemAmount:Int = 0) : LeafTask(bb){

    override fun check(): Boolean {
        return bb.targetEntity != null
    }

    override fun start() {
        val inv = Mappers.inventory.get(bb.targetEntity)
        val production:DefinitionManager.Production

        //The the item name is empty, try to pull a production from the target entity ( ie: the building we're producing at)
        if(itemName == ""){
            val producesItem = Mappers.produces.get(bb.targetEntity)
            production = producesItem.productionList[producesItem.currProductionIndex]
            producesItem.currProductionIndex = (producesItem.currProductionIndex +1)%producesItem.productionList.size

            itemAmount = production.produceAmount
        //Otherwise, use the name that came in
        }else{
            production = DefinitionManager.productionMap[itemName]!!
        }

        //If our inventory doesn't have enough
        //Set this to false and break since we don't have a required amount
        val hasAllItems = production!!.requirements
                .none { inv.getItemAmount(it.itemName) < it.itemAmount }

        if(hasAllItems) {
            production.requirements
                    .forEach { inv.removeItem(it.itemName, it.itemAmount) }


            inv.addItem(production.producedItem, itemAmount)
            controller.finishWithSuccess()
        }else{
            controller.finishWithFailure()
        }

    }
}