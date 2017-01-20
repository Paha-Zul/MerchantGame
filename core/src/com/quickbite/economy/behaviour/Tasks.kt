package com.quickbite.economy.behaviour

import com.quickbite.economy.behaviour.leaf.*

/**
 * Created by Paha on 1/16/2017.
 */
object Tasks {

    fun testMove(bb:BlackBoard):Task{
        val seq = com.quickbite.economy.behaviour.composite.Sequence(bb)

        val getPath = GetPath(bb)
        val moveTo = MoveToPath(bb)

        seq.controller.AddTask(getPath)
        seq.controller.AddTask(moveTo)

        return seq
    }

    fun buyItemFromBuilding(bb:BlackBoard):Task{
        val seq = com.quickbite.economy.behaviour.composite.Sequence(bb)

        val getBuilding = GetClosestBuildingSellingItem(bb, "Wood Plank")
        val getEntrace = GetEntranceOfBuilding(bb)
        val getPath = GetPath(bb)
        val moveTo = MoveToPath(bb)
        val hide = ChangeHidden(bb, true)
        val wait = Wait(bb)
        val buyItem = BuyItem(bb)
        val unhide = ChangeHidden(bb, false)
        val getExit = GetMapExit(bb)
        val getPathToExit = GetPath(bb)
        val moveToExit = MoveToPath(bb)
        val destroyMyself = DestroyMyself(bb)

        seq.controller.AddTask(getBuilding)
        seq.controller.AddTask(getEntrace)
        seq.controller.AddTask(getPath)
        seq.controller.AddTask(moveTo)
        seq.controller.AddTask(hide)
        seq.controller.AddTask(wait)
        seq.controller.AddTask(buyItem)
        seq.controller.AddTask(unhide)
        seq.controller.AddTask(getExit)
        seq.controller.AddTask(getPathToExit)
        seq.controller.AddTask(moveToExit)
        seq.controller.AddTask(destroyMyself)

        return seq
    }
}