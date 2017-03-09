package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.utils.Array
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.InventoryComponent
import com.quickbite.economy.components.ResellingItemsComponent
import com.quickbite.economy.util.EntityListLink
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/27/2017.
 */
class GetClosestShopLinkWithItem(bb:BlackBoard, val itemName:String, val itemAmount:Int = 1) : LeafTask(bb) {
    lateinit var links:Array<EntityListLink>
    lateinit var linkInv:InventoryComponent

    override fun check(): Boolean {
        var shop:ResellingItemsComponent? = null
        if(bb.targetEntity != null){
            shop = Mappers.reselling.get(bb.targetEntity)
            links = shop.resellingEntityItemLinks
            linkInv = Mappers.inventory.get(bb.targetEntity)
        }

        return bb.targetEntity != null && shop != null
    }

    override fun start() {
        //For each entity -> item link
        links.forEach { entityLink ->
            //For each item -> price link
            entityLink.list.forEach { itemLink ->
                //If the item we wanted matches a link AND the shop inventory contains it, success!
                if(itemLink.itemName == this.itemName && linkInv.hasItem(itemName)){
                    controller.finishWithSuccess()
                    return
                }
            }
        }

        controller.finishWithFailure()
    }
}