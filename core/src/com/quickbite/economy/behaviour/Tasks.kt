package com.quickbite.economy.behaviour

import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.behaviour.composite.Sequence
import com.quickbite.economy.behaviour.decorator.AlwaysTrue
import com.quickbite.economy.behaviour.decorator.RepeatTaskNumberOfTimes
import com.quickbite.economy.behaviour.decorator.RepeatUntilFail
import com.quickbite.economy.behaviour.decorator.SucceedOpposite
import com.quickbite.economy.behaviour.leaf.*
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

    fun tendToPlant(bb:BlackBoard) : Task{
        val seq = Sequence(bb)

        val setMyTarget = SetMyWorkBuildingAsTarget(bb)
        val getPlant = GetPlant(bb, "tend")
        val getPath = GetPath(bb)
        val moveToPlant = MoveToPath(bb)
        val tendToPlant = TendToPlant(bb)

        seq.controller.addTasks(setMyTarget, getPlant, getPath, moveToPlant, tendToPlant)

        return seq
    }

    fun harvestPlant(bb:BlackBoard) : Task{
        val seq = Sequence(bb)

        val setMyTarget = SetMyWorkBuildingAsTarget(bb)
        val getPlant = GetPlant(bb, "harvest")
        val getPath = GetPath(bb)
        val moveToPlant = MoveToPath(bb)
        val harvestPlant = HarvestPlant(bb)

        seq.controller.addTasks(setMyTarget, getPlant, getPath, moveToPlant, harvestPlant)

        return seq
    }

    fun farm(bb:BlackBoard):Task{
        val seq = Sequence(bb)

        val tendAndHarvest = Sequence(bb)

        val unhide = ExitBuilding(bb)
        val alwaysTrueTend = AlwaysTrue(bb, tendToPlant(bb))
        val alwaysTrueHarvest = AlwaysTrue(bb, harvestPlant(bb))

        tendAndHarvest.controller.addTasks(unhide, alwaysTrueTend, alwaysTrueHarvest)

        val repeatTasks = RepeatTaskNumberOfTimes(bb, 10, tendAndHarvest)
        val transferInventory = haulInventoryToMyWorkBuilding(bb)

        seq.controller.addTasks(repeatTasks, transferInventory)

        return seq
    }

    /**
     * Finds the closest resource, harvests it, and bring it back to a building to transfer
     */
    fun harvestClosestResourceType(bb:BlackBoard) : Task{
        val seq = Sequence(bb, "Harvesting Resource")

        val leaveBuilding = ExitBuilding(bb)
        val getResource = GetClosestResourceWithHarvesterSpot(bb)
        val getResourceHarvestSpot = GetSpotOfEntity(bb, "harvest")
        val getPathToResource = GetPath(bb)
        val moveToResource = MoveToPath(bb)
        val harvest = HarvestResource(bb)
        val releaseResource = ReleaseResourceHarvesterSpot(bb)
        val getMyWorkshop = SetMyWorkBuildingAsTarget(bb)
        val getEntranceOfBuilding = GetSpotOfEntity(bb, "entrance")
        val getPathToMyBuilding = GetPath(bb)
        val moveToMyBuilding = MoveToPath(bb)
        val enterBuilding = EnterBuilding(bb)
        val transferAll = TransferFromInventoryToInventory(bb, true, true)

        seq.controller.addTask(leaveBuilding)
        seq.controller.addTask(getResource)
        seq.controller.addTask(getResourceHarvestSpot)
        seq.controller.addTask(getPathToResource)
        seq.controller.addTask(moveToResource)
        seq.controller.addTask(harvest)
        seq.controller.addTask(releaseResource)
        seq.controller.addTask(getMyWorkshop)
        seq.controller.addTask(getEntranceOfBuilding)
        seq.controller.addTask(getPathToMyBuilding)
        seq.controller.addTask(moveToMyBuilding)
        seq.controller.addTask(enterBuilding)
        seq.controller.addTask(transferAll)

        return seq
    }

    /**
     * Constructs a task to buy an item (from the buyer demands) from a building. Once the item is bought the pawn will
     * unhide and do nothing. This task is mostly for helping to build other taskList.
     */
    fun buyItemDemandFromBuilding(bb:BlackBoard):Task{
        val seq = com.quickbite.economy.behaviour.composite.Sequence(bb)

        val leaveInitialBuilding = ExitBuilding(bb)
        val getItemDemand = SetTargetItemAndEntityFromDemand(bb)
        val getEntrance = GetEntranceOfBuilding(bb)
        val getPath = GetPath(bb)
        val moveTo = MoveToPath(bb)
        val enterBuilding = EnterBuilding(bb)
        val setInside = SetTargetEntityAsInside(bb)
        val enterBuildingQueue = EnterBuildingQueue(bb)
        val buyItem = WaitTimeOrCondition(bb, MathUtils.random(15f, 25f), {ent -> Mappers.buyer.get(ent).buyerFlag != BuyerComponent.BuyerFlag.None})
        val handleBought = HandleBuyStatus(bb)
        val leaveBuildingQueue = LeaveBuildingQueue(bb)
        val leaveBuilding = ExitBuilding(bb)
        val setOutside = SetTargetEntityAsInside(bb, false)

        seq.controller.addTask(leaveInitialBuilding)
        seq.controller.addTask(getItemDemand)
        seq.controller.addTask(getEntrance)
        seq.controller.addTask(getPath)
        seq.controller.addTask(moveTo)
        seq.controller.addTask(enterBuilding)
        seq.controller.addTask(setInside)
        seq.controller.addTask(enterBuildingQueue)
        seq.controller.addTask(buyItem)
        seq.controller.addTask(handleBought)
        seq.controller.addTask(leaveBuildingQueue)
        seq.controller.addTask(leaveBuilding)
        seq.controller.addTask(setOutside)

        return seq
    }

    fun tryToBuyAllItemDemands(bb:BlackBoard):Task{
        val task = RepeatUntilFail(bb, buyItemDemandFromBuilding(bb))
        return task
    }

    /**
     * Finds the exit and leaves the map
     */
    fun leaveMap(bb:BlackBoard):Task{
        val seq = Sequence(bb, "Leaving Map")

        val leaveBuilding = ExitBuilding(bb)
        val getExit = GetMapExit(bb)
        val getPathToExit = GetPath(bb)
        val moveToExit = MoveToPath(bb)
        val applyRatingsToTown = ApplyRatingsToTown(bb)
        val destroyMyself = DestroyMyself(bb)

        seq.controller.addTask(leaveBuilding)
        seq.controller.addTask(getExit)
        seq.controller.addTask(getPathToExit)
        seq.controller.addTask(moveToExit)
        seq.controller.addTask(applyRatingsToTown)
        seq.controller.addTask(destroyMyself)

        return seq
    }

    fun leaveMapAndHide(bb:BlackBoard):Task{
        val seq = Sequence(bb, "Leaving Map")

        val leaveBuilding = ExitBuilding(bb)
        val setOutside = SetTargetEntityAsInside(bb, false)
        val getExit = GetMapExit(bb)
        val getPathToExit = GetPath(bb)
        val moveToExit = MoveToPath(bb)
        val hide = ChangeHidden(bb, true)

        seq.controller.addTask(leaveBuilding)
        seq.controller.addTask(setOutside)
        seq.controller.addTask(getExit)
        seq.controller.addTask(getPathToExit)
        seq.controller.addTask(moveToExit)
        seq.controller.addTask(hide)

        return seq
    }

    /**
     * A task for a pawn to buy an item from somewhere and then leave the map
     */
    fun buyItemDemandAndLeaveMap(bb:BlackBoard, itemName:String = "Wood Log"):Task{
        val seq = Sequence(bb, "Buying Item and Leaving")

        val buyItem = AlwaysTrue(bb, RepeatUntilFail(bb, buyItemDemandFromBuilding(bb)))
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
        val leaveBuilding = ExitBuilding(bb)
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
        seq.controller.addTask(leaveBuilding)
        seq.controller.addTask(getExit)
        seq.controller.addTask(getPathToExit)
        seq.controller.addTask(moveToExit)
        seq.controller.addTask(destroyMyself)

        return seq
    }

    fun haulItemFromBuilding(bb: BlackBoard):Task {
        val task = com.quickbite.economy.behaviour.composite.Sequence(bb)

        //TODO Need to make sure it knows its inside a building....
        val leaveMyBuilding = ExitBuilding(bb)
        val setTargetItem:Task = SetTargetItemToHaul(bb)
        val getBuildingToHaulFrom:Task = GetBuildingToHaulFrom(bb)
        val setTargetAsBuilding = SetTargetEntityAsTargetPosition(bb)
        val getMoneyForHaul = TransferMoneyToInventoryForItemPurchase(bb)
        val getTargetEntrance = GetEntranceOfBuilding(bb)
        val getPathToTarget = GetPath(bb)
        val moveToTarget = MoveToPath(bb)
        val enterHaulingBuilding = EnterBuilding(bb)
        val wait = Wait(bb, 0.5f, 1.5f)
        val transferMoneyToBuildingForHaulMaterials = TransferMoneyToInventoryForItemPurchase(bb, false)
        val transferItemsToMe = TransferFromInventoryToInventory(bb, false) //Transfer items from target to me
        val leaveHaulingBuilding = ExitBuilding(bb)
        val setMyBuildingAsTarget = SetMyWorkBuildingAsTarget(bb)
        val getEntranceOfBuilding = GetEntranceOfBuilding(bb)
        val getPathToBuilding = GetPath(bb)
        val moveToBuilding = MoveToPath(bb)
        val enterMyBuildingAgain = EnterBuilding(bb)
        val waitBeforeTransfer = Wait(bb, 1f, 2f)
        val transferItemsToWorkshop = TransferFromInventoryToInventory(bb, true) //Transfer items from me to target
        val giveAllMoneyBackToWorkerBuilding = TransferMoneyToInventoryForItemPurchase(bb, false, true) //Give all the money back

        task.controller.addTask(leaveMyBuilding)
        task.controller.addTask(setTargetItem)
        task.controller.addTask(getBuildingToHaulFrom)
        task.controller.addTask(setTargetAsBuilding)
        task.controller.addTask(getMoneyForHaul)
        task.controller.addTask(getTargetEntrance)
        task.controller.addTask(getPathToTarget)
        task.controller.addTask(moveToTarget)
        task.controller.addTask(enterHaulingBuilding)
        task.controller.addTask(wait)
        task.controller.addTask(transferMoneyToBuildingForHaulMaterials)
        task.controller.addTask(transferItemsToMe)
        task.controller.addTask(leaveHaulingBuilding)
        task.controller.addTask(setMyBuildingAsTarget)
        task.controller.addTask(getEntranceOfBuilding)
        task.controller.addTask(getPathToBuilding)
        task.controller.addTask(moveToBuilding)
        task.controller.addTask(enterMyBuildingAgain)
        task.controller.addTask(waitBeforeTransfer)
        task.controller.addTask(transferItemsToWorkshop)
        task.controller.addTask(giveAllMoneyBackToWorkerBuilding)

        return task
    }

    fun produceItem(bb:BlackBoard) : Task{
        val task = com.quickbite.economy.behaviour.composite.Sequence(bb)

        val repeatUntilFail = RepeatUntilFail(bb, task)
        val optionalBranchSequence = Sequence(bb)
        val optionalBranch = AlwaysTrue(bb, optionalBranchSequence)

        //TODO Check if inside the building already?

        task.controller.addTask(SetMyWorkBuildingAsTarget(bb))
        task.controller.addTask(optionalBranch)

        //Optional branch. If we are not inside our target building already, move to it
        optionalBranchSequence.controller.addTask(SucceedOpposite(bb, CheckInsideTargetEntity(bb))) //If this succeeds (opposite), we are outside. Continue moving!
        optionalBranchSequence.controller.addTask(SetTargetEntityAsInside(bb))
        optionalBranchSequence.controller.addTask(GetEntranceOfBuilding(bb))
        optionalBranchSequence.controller.addTask(ChangeHidden(bb, false))
        optionalBranchSequence.controller.addTask(GetPath(bb))
        optionalBranchSequence.controller.addTask(MoveToPath(bb))

        task.controller.addTask(ChangeHidden(bb, true))
        task.controller.addTask(Wait(bb))
        task.controller.addTask(ProduceItem(bb))

        return repeatUntilFail
    }

    fun sellItem(bb:BlackBoard) : Task{
        //TODO make it so the entity selling stays in the building and doesn't pop in and out...
        val task = com.quickbite.economy.behaviour.composite.Sequence(bb)

        //Check if inside the building already?
        val optionalBranchSequence = Sequence(bb)
        val optionalBranch = AlwaysTrue(bb, optionalBranchSequence)

        task.controller.addTask(SetMyWorkBuildingAsTarget(bb))
        task.controller.addTask(optionalBranch)

        //Optional branch. If we are not inside our target building, then move to it!
        optionalBranchSequence.controller.addTask(SucceedOpposite(bb, CheckInsideTargetEntity(bb)))
        optionalBranchSequence.controller.addTask(ChangeHidden(bb, false))
        optionalBranchSequence.controller.addTask(GetEntranceOfBuilding(bb))
        optionalBranchSequence.controller.addTask(GetPath(bb))
        optionalBranchSequence.controller.addTask(MoveToPath(bb))
        optionalBranchSequence.controller.addTask(SetTargetEntityAsInside(bb))
        optionalBranchSequence.controller.addTask(CheckBuildingHasQueue(bb))

        task.controller.addTask(ChangeHidden(bb, true))
        task.controller.addTask(Wait(bb, MathUtils.random(0.5f, 3f)))
        task.controller.addTask(SellItemFromBuildingToEnqueued(bb))

        return task
    }

    /**
     * This task will move an Entity to a stockpile, transfer the inventory, and leave the map
     */
    fun haulInventoryToStockpile(bb:BlackBoard):Task{
        val task = com.quickbite.economy.behaviour.composite.Sequence(bb, "Hauling to Stockpile")

        //This is in case something happens to the building we are delivering to... we'll still leave the map if we can't finish it
        val alwaysSucceedSeq = Sequence(bb)
        val alwaysTrueBranch = AlwaysTrue(bb, alwaysSucceedSeq)

        alwaysSucceedSeq.controller.addTask(GetClosestShopResellingViaImport(bb))
        alwaysSucceedSeq.controller.addTask(GetEntranceOfBuilding(bb))
        alwaysSucceedSeq.controller.addTask(GetPath(bb))
        alwaysSucceedSeq.controller.addTask(MoveToPath(bb))
        alwaysSucceedSeq.controller.addTask(EnterBuilding(bb))
        alwaysSucceedSeq.controller.addTask(Wait(bb, MathUtils.random(0.5f, 3f)))
        alwaysSucceedSeq.controller.addTask(TransferFromInventoryToInventory(bb, true, true))

        task.controller.addTask(alwaysTrueBranch) //Leave the map
        task.controller.addTask(leaveMap(bb)) //Leave the map

        return task
    }

    fun haulInventoryToMyWorkBuilding(bb:BlackBoard):Task{
        val task = com.quickbite.economy.behaviour.composite.Sequence(bb, "Hauling to Stockpile")

        //This is in case something happens to the building we are delivering to... we'll still leave the map if we can't finish it
        val alwaysSucceedSeq = Sequence(bb)
        val alwaysTrueBranch = AlwaysTrue(bb, alwaysSucceedSeq)

        alwaysSucceedSeq.controller.addTask(CheckInventoryNotEmpty(bb))
        alwaysSucceedSeq.controller.addTask(SetMyWorkBuildingAsTarget(bb))
        alwaysSucceedSeq.controller.addTask(GetEntranceOfBuilding(bb))
        alwaysSucceedSeq.controller.addTask(GetPath(bb))
        alwaysSucceedSeq.controller.addTask(MoveToPath(bb))
        alwaysSucceedSeq.controller.addTask(EnterBuilding(bb))
        alwaysSucceedSeq.controller.addTask(TransferFromInventoryToInventory(bb, true, true))
        alwaysSucceedSeq.controller.addTask(ExitBuilding(bb))

        task.controller.addTask(alwaysTrueBranch) //Leave the map

        return task
    }

    fun moveToMyBuilding(bb:BlackBoard) : Task{
        val task = com.quickbite.economy.behaviour.composite.Sequence(bb, "Moving")

        val setMyBuildingAsTarget = SetMyWorkBuildingAsTarget(bb)
        val getEntranceOfBuilding = GetEntranceOfBuilding(bb)
        val getPathToBuilding = GetPath(bb)
        val moveToBuilding = MoveToPath(bb)
        val enterMyBuildingAgain = EnterBuilding(bb)

        task.controller.addTasks(setMyBuildingAsTarget, getEntranceOfBuilding, getPathToBuilding, moveToBuilding, enterMyBuildingAgain)

        return task
    }

    fun haulWorkerTask(bb:BlackBoard) : Task{
        val task = com.quickbite.economy.behaviour.composite.Selector(bb, "Hauling Work")

        task.controller.addTasks(haulItemFromBuilding(bb), moveToMyBuilding(bb))

        return task
    }
}