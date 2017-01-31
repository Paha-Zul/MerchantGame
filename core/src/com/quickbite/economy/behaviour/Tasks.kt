package com.quickbite.economy.behaviour

import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.behaviour.leaf.*
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.components.BuyerComponent
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/16/2017.
 */
object Tasks {

    fun testMove(bb:BlackBoard):Task{
        val seq = com.quickbite.economy.behaviour.composite.Sequence(bb)

        val getPath = GetPath(bb)
        val moveTo = MoveToPath(bb)

        seq.controller.addTask(getPath)
        seq.controller.addTask(moveTo)

        return seq
    }

    fun buyItemFromBuilding(bb:BlackBoard, itemName:String = "Wood Plank"):Task{
        val seq = com.quickbite.economy.behaviour.composite.Sequence(bb)

        val getBuilding = GetClosestBuildingSellingItem(bb, itemName)
        val getEntrace = GetEntranceOfBuilding(bb)
        val getPath = GetPath(bb)
        val moveTo = MoveToPath(bb)
        val hide = ChangeHidden(bb, true)
        val enterBuildingQueue = EnterBuildingQueue(bb)
        val buyItem = WaitTimeOrCondition(bb, MathUtils.random(15f, 25f), {ent -> Mappers.buyer.get(ent).buyerFlag != BuyerComponent.BuyerFlag.None})
        val leaveBuildingQueue = LeaveBuildingQueue(bb)
        val unhide = ChangeHidden(bb, false)
        val getExit = GetMapExit(bb)
        val getPathToExit = GetPath(bb)
        val moveToExit = MoveToPath(bb)
        val destroyMyself = DestroyMyself(bb)

        seq.controller.addTask(getBuilding)
        seq.controller.addTask(getEntrace)
        seq.controller.addTask(getPath)
        seq.controller.addTask(moveTo)
        seq.controller.addTask(hide)
        seq.controller.addTask(enterBuildingQueue)
        seq.controller.addTask(buyItem)
        seq.controller.addTask(leaveBuildingQueue)
        seq.controller.addTask(unhide)
        seq.controller.addTask(getExit)
        seq.controller.addTask(getPathToExit)
        seq.controller.addTask(moveToExit)
        seq.controller.addTask(destroyMyself)

        return seq
    }

    fun haulItemFromBuilding(bb:BlackBoard, buildingType: BuildingComponent.BuildingType, itemName:String, itemAmount:Int):Task {
        val task = com.quickbite.economy.behaviour.composite.Sequence(bb)

        val unhide = ChangeHidden(bb, false)

        val getStockpile:Task
        if(buildingType == BuildingComponent.BuildingType.Shop){
            getStockpile = GetClosestShopLinkWithItem(bb, itemName)
        }else
            getStockpile = GetClosestBuildingWithItem(bb, buildingType, itemName)

        val setStockpileTarget = SetTargetEntityAsTargetPosition(bb)
        val getStockpileEntrance = GetEntranceOfBuilding(bb)
        val getPathToStockpile = GetPath(bb)
        val moveToStockpile = MoveToPath(bb)
        val hide = ChangeHidden(bb, true)
        val wait = Wait(bb, 0.5f)
        val transferItemsToMe = TransferFromInventoryToInventory(bb, false, itemName, itemAmount) //Transfer items from target to me
        val unhideAgain = ChangeHidden(bb, false)
        val setMyBuildingAsTarget = SetMyWorkBuildingAsTarget(bb)
        val getEntranceOfBuilding = GetEntranceOfBuilding(bb)
        val getPathToBuilding = GetPath(bb)
        val moveToBuilding = MoveToPath(bb)
        val hideAgain = ChangeHidden(bb, true)
        val waitBeforeTransfer = Wait(bb)
        val transferItemsToWorkshop = TransferFromInventoryToInventory(bb, true, itemName, itemAmount) //Transfer items from target to me
        val unhideAgainAgain = ChangeHidden(bb, true)

        task.controller.addTask(unhide)
        task.controller.addTask(getStockpile)
        task.controller.addTask(setStockpileTarget)
        task.controller.addTask(getStockpileEntrance)
        task.controller.addTask(getPathToStockpile)
        task.controller.addTask(moveToStockpile)
        task.controller.addTask(hide)
        task.controller.addTask(wait)
        task.controller.addTask(transferItemsToMe)
        task.controller.addTask(unhideAgain)
        task.controller.addTask(setMyBuildingAsTarget)
        task.controller.addTask(getEntranceOfBuilding)
        task.controller.addTask(getPathToBuilding)
        task.controller.addTask(moveToBuilding)
        task.controller.addTask(hideAgain)
        task.controller.addTask(waitBeforeTransfer)
        task.controller.addTask(transferItemsToWorkshop)
        task.controller.addTask(unhideAgainAgain)

        return task
    }

    fun produceItem(bb:BlackBoard) : Task{
        val task = com.quickbite.economy.behaviour.composite.Sequence(bb)

        //TODO Check if inside the building already?

        task.controller.addTask(SetMyWorkBuildingAsTarget(bb))
        task.controller.addTask(GetEntranceOfBuilding(bb))
        task.controller.addTask(GetPath(bb))
        task.controller.addTask(MoveToPath(bb))
        task.controller.addTask(ChangeHidden(bb, true))
        task.controller.addTask(Wait(bb))
        task.controller.addTask(ProduceItem(bb, "Wood Plank", 10))

        return task
    }

    fun sellItem(bb:BlackBoard) : Task{
        val task = com.quickbite.economy.behaviour.composite.Sequence(bb)

        //Check if inside the building already?

        task.controller.addTask(SetMyWorkBuildingAsTarget(bb))
        task.controller.addTask(CheckBuildingHasQueue(bb))
        task.controller.addTask(GetEntranceOfBuilding(bb))
        task.controller.addTask(GetPath(bb))
        task.controller.addTask(MoveToPath(bb))
        task.controller.addTask(ChangeHidden(bb, true))
        task.controller.addTask(Wait(bb, MathUtils.random(0.5f, 3f)))
        task.controller.addTask(SellItemFromBuildingToEnqueued(bb))

        return task
    }
}