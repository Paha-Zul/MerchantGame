package com.quickbite.economy.behaviour

import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.behaviour.composite.Sequence
import com.quickbite.economy.behaviour.decorator.RepeatUntilFail
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

    /**
     * Constructs a task to buy an item (from the buyer demands) from a building. Once the item is bought the pawn will
     * unhide and do nothing. This task is mostly for helping to build other tasks.
     */
    fun buyItemDemandFromBuilding(bb:BlackBoard):Task{
        val seq = com.quickbite.economy.behaviour.composite.Sequence(bb)

        //TODO Need to get the demand from the pawn to get the closest building with item (from demands)
        val getItemDemand = SetTargetItemFromDemand(bb)
        val getBuilding = GetClosestBuildingSellingItem(bb)
        val getEntrance = GetEntranceOfBuilding(bb)
        val getPath = GetPath(bb)
        val moveTo = MoveToPath(bb)
        val hide = ChangeHidden(bb, true)
        val enterBuildingQueue = EnterBuildingQueue(bb)
        val buyItem = WaitTimeOrCondition(bb, MathUtils.random(15f, 25f), {ent -> Mappers.buyer.get(ent).buyerFlag != BuyerComponent.BuyerFlag.None})
        val leaveBuildingQueue = LeaveBuildingQueue(bb)
        val unhide = ChangeHidden(bb, false)

        seq.controller.addTask(getItemDemand)
        seq.controller.addTask(getBuilding)
        seq.controller.addTask(getEntrance)
        seq.controller.addTask(getPath)
        seq.controller.addTask(moveTo)
        seq.controller.addTask(hide)
        seq.controller.addTask(enterBuildingQueue)
        seq.controller.addTask(buyItem)
        seq.controller.addTask(leaveBuildingQueue)
        seq.controller.addTask(unhide)

        return seq
    }

    /**
     * Finds the exit and leaves the map
     */
    fun leaveMap(bb:BlackBoard):Task{
        val seq = Sequence(bb, "Leaving Map")

        val unhide = ChangeHidden(bb, false)
        val getExit = GetMapExit(bb)
        val getPathToExit = GetPath(bb)
        val moveToExit = MoveToPath(bb)
        val destroyMyself = DestroyMyself(bb)

        seq.controller.addTask(unhide)
        seq.controller.addTask(getExit)
        seq.controller.addTask(getPathToExit)
        seq.controller.addTask(moveToExit)
        seq.controller.addTask(destroyMyself)

        return seq
    }

    /**
     * A task for a pawn to buy an item from somewhere and then leave the map
     */
    fun buyItemDemandAndLeaveMap(bb:BlackBoard, itemName:String = "Wood Log"):Task{
        val seq = Sequence(bb, "Buying Item and Leaving")

        val buyItem = buyItemDemandFromBuilding(bb)
        val leaveMap = leaveMap(bb)

        seq.controller.addTask(buyItem)
        seq.controller.addTask(leaveMap)

        return seq
    }

    /**
     * A task meant for pawns to buy from a shop and haul back to their workshop.
     */
    fun buyItemAndHaulBack(bb:BlackBoard){

    }

    fun wanderToBuildings(bb:BlackBoard):Task{
        val seq = com.quickbite.economy.behaviour.composite.Sequence(bb)

        val getBuilding = GetRandomBuildingThatSellsItems(bb)
        val getEntrace = GetEntranceOfBuilding(bb)
        val getPath = GetPath(bb)
        val moveTo = MoveToPath(bb)
        val hide = ChangeHidden(bb, true)
        val wait = Wait(bb, 1f, 5f)
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
        seq.controller.addTask(wait)
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

        val setTargetItem:Task
        val getStockpile:Task

        if(buildingType == BuildingComponent.BuildingType.Shop){
            setTargetItem = SetTargetItem(bb, itemName)
            getStockpile = GetClosestShopLinkWithItem(bb, itemName)
        }else {
            setTargetItem = SetTargetItemFromMyWorkshop(bb)
            getStockpile = GetClosestBuildingWithItem(bb, buildingType)
        }

        val setStockpileTarget = SetTargetEntityAsTargetPosition(bb)
        val getStockpileEntrance = GetEntranceOfBuilding(bb)
        val getPathToStockpile = GetPath(bb)
        val moveToStockpile = MoveToPath(bb)
        val hide = ChangeHidden(bb, true)
        val wait = Wait(bb, 0.5f)
        val transferItemsToMe = TransferFromInventoryToInventory(bb, false) //Transfer items from target to me
        val unhideAgain = ChangeHidden(bb, false)
        val setMyBuildingAsTarget = SetMyWorkBuildingAsTarget(bb)
        val getEntranceOfBuilding = GetEntranceOfBuilding(bb)
        val getPathToBuilding = GetPath(bb)
        val moveToBuilding = MoveToPath(bb)
        val hideAgain = ChangeHidden(bb, true)
        val waitBeforeTransfer = Wait(bb)
        //TODO Set the target item here
        val transferItemsToWorkshop = TransferFromInventoryToInventory(bb, true) //Transfer items from me to target
        val unhideAgainAgain = ChangeHidden(bb, true)

        task.controller.addTask(unhide)
        task.controller.addTask(setTargetItem)
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

        val repeatUntilFail = RepeatUntilFail(bb, task)

        //TODO Check if inside the building already?

        task.controller.addTask(SetMyWorkBuildingAsTarget(bb))
        task.controller.addTask(GetEntranceOfBuilding(bb))
        task.controller.addTask(GetPath(bb))
        task.controller.addTask(MoveToPath(bb))
        task.controller.addTask(ChangeHidden(bb, true))
        task.controller.addTask(Wait(bb))
        task.controller.addTask(ProduceItem(bb))

        return repeatUntilFail
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