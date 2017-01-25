package com.quickbite.economy.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.Task
import com.quickbite.economy.behaviour.Tasks
import com.quickbite.economy.behaviour.decorator.AlwaysTrue
import com.quickbite.economy.components.BehaviourComponent
import com.quickbite.economy.components.WorkForceComponent
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/22/2017.
 */
class WorkshopSystem(interval:Float) : IntervalIteratingSystem(Family.all(WorkForceComponent::class.java).get(), interval){

    override fun processEntity(ent: Entity) {
        val wc = Mappers.workforce.get(ent)

        wc.workersAvailable.forEachIndexed { index, worker ->
            val bc = Mappers.behaviour.get(worker)
            if(bc.isIdle){
                val tasks = wc.workerTasks[wc.workersAvailable.size-1][index].split(",")
                val task = assignTasks(tasks, bc)
                bc.currTask = task
            }
        }
    }

    private fun assignTasks(tasks:List<String>, bc: BehaviourComponent):Task{
        val task = com.quickbite.economy.behaviour.composite.Sequence(bc.blackBoard)

        tasks.forEach { taskName ->
            task.controller.addTask(getTask(taskName.trim(), bc.blackBoard))
        }

        return task
    }

    private fun getTask(task:String, bb:BlackBoard):Task{
        when(task){
            "haul" -> return AlwaysTrue(bb, Tasks.haulItemFromStockToBuilding(bb, "Wood Log", 10))
            "produce" -> return AlwaysTrue(bb, Tasks.produceItem(bb))
            "sell" -> return AlwaysTrue(bb, com.quickbite.economy.behaviour.composite.Sequence(bb, "Empty Sequence"))
        }

        return com.quickbite.economy.behaviour.composite.Sequence(bb, "Empty Sequence")
    }
}