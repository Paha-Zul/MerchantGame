package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.objects.SellingItemData
import com.quickbite.economy.util.FindEntityUtil
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 5/4/2017.
 * Specifically finds a shop that is reselling a certain item via import method. If the itemName isn't specified it
 * uses the bb.targetItem
 * @param bb The Blackboard to use
 * @param itemName Optional item name. If not used, uses bb.targetItem
 */
class GetClosestShopResellingViaImport(bb:BlackBoard, var itemName:String = "") : LeafTask(bb) {

    override fun start() {
        super.start()

        if(itemName == "")
            itemName = bb.targetItem.itemName.toLowerCase()

        val building = FindEntityUtil.getClosestBuildingType(Mappers.transform.get(bb.myself).position, BuildingComponent.BuildingType.Shop, {
            val selling = Mappers.selling[it]
            selling.currSellingItems.any { it.itemName == itemName && it.itemSourceType == SellingItemData.ItemSource.Import }
        })

        if(building!= null){
            bb.targetPosition = Vector2(Mappers.transform.get(building).position)
            bb.targetEntity = building
            bb.targetBuilding = Mappers.building.get(building)

            controller.finishWithSuccess()
        }else
            controller.finishWithFailure()
    }
}