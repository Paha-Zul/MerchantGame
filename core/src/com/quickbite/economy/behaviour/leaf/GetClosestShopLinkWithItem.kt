package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.utils.Array
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.EntityListLink
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/27/2017.
 *
 * Attempts to get the closest Entity from the shop link that has the item we are requesting
 */
class GetClosestShopLinkWithItem(bb:BlackBoard, var itemName:String = "", var itemAmount:Int = 1) : LeafTask(bb) {
    lateinit var links:Array<EntityListLink>

    override fun check(): Boolean {
        val worker = Mappers.worker[bb.myself]
        val sellingComp = Mappers.selling.get(worker.workerBuilding)
        links = sellingComp.resellingEntityItemLinks

        return sellingComp != null
    }

    override fun start() {
        if(itemName == ""){
            itemName = bb.targetItem.itemName
            itemAmount = bb.targetItem.itemAmount
        }

        //Here we are going to search through each entity link and find if the entity has the item we want...

        //For each entity, check it's itemPriceLinkList of items
        links.forEach { (entity, itemPriceLinkList) ->
            val inventory = Mappers.inventory[entity]
            //For each item, check if the item matches what we want and the
            itemPriceLinkList.forEach { itemLink ->
                //If the item we wanted matches a link AND the building has the item, success!
                if(itemLink.itemName == this.itemName && inventory.hasItem(itemName)){
                    bb.targetEntity = entity
                    controller.finishWithSuccess()
                    return
                }
            }
        }

        controller.finishWithFailure()
    }
}