package com.quickbite.economy.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.Task
import com.quickbite.economy.behaviour.Tasks
import com.quickbite.economy.behaviour.decorator.AlwaysTrue
import com.quickbite.economy.components.BehaviourComponent
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.components.WorkForceComponent
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.TimeOfDay

/**
 * Created by Paha on 1/22/2017.
 */
class WorkforceSystem(interval:Float) : IntervalIteratingSystem(Family.all(WorkForceComponent::class.java).get(), interval){
    var paidWorkers = true

    override fun processEntity(ent: Entity) {
        val wc = Mappers.workforce.get(ent)
        val building = Mappers.building.get(ent)

        wc.workersAvailable.forEachIndexed { index, workerEntity ->
            val bc = Mappers.behaviour.get(workerEntity)
            val worker = Mappers.worker[workerEntity]

            if(bc.isIdle){
                //TODO this is under construction

                val tasks = worker.taskList.toList()
                val task = assignTasks(tasks, bc)
                bc.currTask = task

                //TODO Deal with the time schedule

//                //If we are withing the working hour range
//                if(workerTaskLink.timeRange.first <= TimeOfDay.hour && workerTaskLink.timeRange.second >= TimeOfDay.hour) {
//                    val taskList = workerTaskLink.taskList.toList()
//                    val task = assignTasks(taskList, bc)
//                    bc.currTask = task
//
//                //Otherwise
//                }else{
//                    val task = Tasks.leaveMapAndHide(bc.blackBoard)
//                    bc.currTask = task
//                }
            }
        }

        if(TimeOfDay.hour <= 1 && !paidWorkers){
            wc.workersAvailable.forEach { entity ->
                val worker = Mappers.worker[entity]
                val workerBuildingInv = Mappers.inventory[worker.workerBuilding]

                val moneyPaid = workerBuildingInv.removeItem("Gold", worker.dailyWage)
                worker.paid = moneyPaid == worker.dailyWage
            }

            paidWorkers = true
        }else if(TimeOfDay.hour > 1){
            paidWorkers = false
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
        when (task) {
            "haul" -> return AlwaysTrue(bb, Tasks.haulItemFromBuilding(bb, BuildingComponent.BuildingType.Stockpile, "Wood Log", 10))
            "produce" -> return AlwaysTrue(bb, Tasks.produceItem(bb))
            "sell" -> return AlwaysTrue(bb, Tasks.sellItem(bb))
        }

        return com.quickbite.economy.behaviour.composite.Sequence(bb, "Empty Sequence")
    }
}