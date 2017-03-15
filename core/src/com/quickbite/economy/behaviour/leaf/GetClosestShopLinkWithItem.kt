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

        //TODO Should this actually be going through each entity link? What about our target item and target entity in the blackboard?
        //For each entity, check it's itemPriceLinkList of items
        links.forEach { entityLink ->
            val inventory = Mappers.inventory[entityLink.entity]

            //For each item, check if the item matches what we want and the
            entityLink.itemPriceLinkList.forEach { itemLink ->
                //TODO Check against the bb.targetItem and not just for any item?
                //If the item we wanted matches a link AND the building has the item, success!
                if(itemLink.itemName == this.itemName && inventory.hasItem(itemName)){
                    bb.targetEntity = entityLink.entity
                    controller.finishWithSuccess()
                    return
                }
            }
        }

        controller.finishWithFailure()
    }
}