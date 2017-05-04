package com.quickbite.economy.behaviour.leaf

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.objects.SellingItemData
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/27/2017.
 *
 * Attempts to get the closest Entity from the shop link that has the item we are requesting
 */
class GetClosestShopLinkWithItem(bb:BlackBoard, var itemName:String = "", var itemAmount:Int = 1) : LeafTask(bb) {
    lateinit var links:Array<SellingItemData>

    override fun check(): Boolean {
        val worker = Mappers.worker[bb.myself]
        val sellingComp = Mappers.selling.get(worker.workerBuilding)
        links = sellingComp.resellingItemsList

        return sellingComp != null
    }

    override fun start() {
        if(itemName == ""){
            itemName = bb.targetItem.itemName
            itemAmount = bb.targetItem.itemAmount
        }

        //For each entity, check it's itemPriceLinkList of items
        links.forEach { (itemName1, _, _, itemSourceType, itemSourceData) ->
            //Make sure we are getting a workshop item source...
            if(itemSourceType == SellingItemData.ItemSource.Workshop) {
                val inventory = Mappers.inventory[itemSourceData as Entity]
                //If the item we wanted matches a link AND the building has the item, success!
                if (itemName1 == this.itemName && inventory.hasItem(itemName)) {
                    bb.targetEntity = itemSourceData as Entity
                    controller.finishWithSuccess()
                    return
                }
            }
        }

        controller.finishWithFailure()
    }
}