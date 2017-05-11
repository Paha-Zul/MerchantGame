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
import com.quickbite.economy.util.TimeOfDay

/**
 * Created by Paha on 1/22/2017.
 */
class WorkforceSystem(interval:Float) : IntervalIteratingSystem(Family.all(WorkForceComponent::class.java).get(), interval){
    override fun processEntity(ent: Entity) {
        val wc = Mappers.workforce.get(ent)
        val building = Mappers.building.get(ent)

        wc.workersAvailable.forEachIndexed { _, workerEntity ->
            val bc = Mappers.behaviour.get(workerEntity)
            val worker = Mappers.worker[workerEntity]

            if(bc.isIdle){
                //TODO this is under construction

                val tasks = worker.taskList.toList()
                val task = assignTasks(tasks, bc)
                bc.currTask = task

                //TODO Deal with the time schedule

                val diff = 24 - worker.timeRange.first //We get how far from 0 we are (24 == 0 in hours)
                val scaledTime = (TimeOfDay.hour + diff)%24 //We add the diff to the current time to get the scaled time (ie: 22 + 6 % 24 = 4)
                val scaledWorkerTimeStart = (worker.timeRange.first + diff)%24 //This should always be 0 but we do this for consistency
                val scaledWorkerTimeEnd = (worker.timeRange.second + diff)%24 //This will be the ending time, ie: 6 (after scaling)

                //If we are withing the working hour range, assign our person the regular tasks
                if(scaledTime in scaledWorkerTimeStart..scaledWorkerTimeEnd) {
                    val taskList = worker.taskList.toList()
                    val task = assignTasks(taskList, bc)
                    bc.currTask = task

                //Otherwise, assign us a leave the map task
                }else{
                    val task = Tasks.leaveMapAndHide(bc.blackBoard)
                    bc.currTask = task
                }
            }
        }

        //This pays the worker...
        if(TimeOfDay.hour <= 2 && !wc.workersPaidFlag){
            val workerBuildingInv = Mappers.inventory[ent]
            wc.workersAvailable.forEach { entity ->
                val worker = Mappers.worker[entity]

                val moneyPaid = workerBuildingInv.removeItem("Gold", worker.dailyWage)
                worker.paid = moneyPaid == worker.dailyWage
            }

            wc.workersPaidFlag = true
        }else if(TimeOfDay.hour > 2){
            wc.workersPaidFlag = false
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
            "haul" -> return AlwaysTrue(bb, Tasks.haulItemFromBuilding(bb))
            "produce" -> return AlwaysTrue(bb, Tasks.produceItem(bb))
            "sell" -> return AlwaysTrue(bb, Tasks.sellItem(bb))
        }

        return com.quickbite.economy.behaviour.composite.Sequence(bb, "Empty Sequence")
    }
}