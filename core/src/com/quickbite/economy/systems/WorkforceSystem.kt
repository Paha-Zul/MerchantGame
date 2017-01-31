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

/**
 * Created by Paha on 1/22/2017.
 */
class WorkforceSystem(interval:Float) : IntervalIteratingSystem(Family.all(WorkForceComponent::class.java).get(), interval){

    override fun processEntity(ent: Entity) {
        val wc = Mappers.workforce.get(ent)
        val building = Mappers.building.get(ent)

        wc.workersAvailable.forEachIndexed { index, worker ->
            val bc = Mappers.behaviour.get(worker)
            if(bc.isIdle){
                val numWorkersIndex = Math.min(wc.workerTasks.size-1, wc.workersAvailable.size-1)
                val workerIndex = Math.min(wc.workerTasks[numWorkersIndex].size-1, index)

                val tasks = wc.workerTasks[numWorkersIndex][workerIndex].split(",")
                val task = assignTasks(building.buildingType, tasks, bc)
                bc.currTask = task
            }
        }
    }

    private fun assignTasks(buildingType:BuildingComponent.BuildingType, tasks:List<String>, bc: BehaviourComponent):Task{
        val task = com.quickbite.economy.behaviour.composite.Sequence(bc.blackBoard)

        tasks.forEach { taskName ->
            task.controller.addTask(getTask(buildingType, taskName.trim(), bc.blackBoard))
        }

        return task
    }

    private fun getTask(buildingType: BuildingComponent.BuildingType, task:String, bb:BlackBoard):Task{
        when(buildingType) {
            BuildingComponent.BuildingType.Workshop ->
                when (task) {
                    "haul" -> return AlwaysTrue(bb, Tasks.haulItemFromBuilding(bb, BuildingComponent.BuildingType.Stockpile, "Wood Log", 10))
                    "produce" -> return AlwaysTrue(bb, Tasks.produceItem(bb))
                    "sell" -> return AlwaysTrue(bb, Tasks.sellItem(bb))
                }

            BuildingComponent.BuildingType.Shop ->
                when (task) {
                    "haul" -> return AlwaysTrue(bb, Tasks.haulItemFromBuilding(bb, BuildingComponent.BuildingType.Workshop, "Wood Plank", 10))
                    "sell" -> return AlwaysTrue(bb, Tasks.sellItem(bb))
                }
        }

        return com.quickbite.economy.behaviour.composite.Sequence(bb, "Empty Sequence")
    }
}