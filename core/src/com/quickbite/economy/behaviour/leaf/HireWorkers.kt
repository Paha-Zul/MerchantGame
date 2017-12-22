package com.quickbite.economy.behaviour.leaf

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.WorkForceComponent
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

class HireWorkers(bb:BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()
    }

    override fun update(delta: Float) {
        super.update(delta)

        // - Get how many employees we have
        // - Figure out our supply/demand in recent history. Are we not selling fast enough to customers? Are we producing too much/little?
        // - Figure out our recent profit trend. If we are losing money and there is no clear reason why, hiring people won't help. Maybe overall demand is too low?
        // - Find a suitable available worker. Skills/wage and such
        // - Hire him onboard!

        val manager = bb.myself
        val workBuilding = Mappers.worker[manager].workerBuilding!!
        val workForce = Mappers.workforce[workBuilding]
        val workerAmounts = getCurrentWorkerAmounts(workForce)

        val orderedList = workerAmounts.values.toList().sortedBy { it.amount }

        orderedList.forEach {
            if(it.amount < it.max){
                val worker = Util.createAndAssignWorkerToBuilding(workBuilding)!!
                Util.toggleTaskOnWorker(worker, workBuilding, it.name)
                return@forEach
            }
        }

        controller.finishWithSuccess()

//        if(workerAmounts["haul"] == 0){
//            val worker = Util.createAndAssignWorkerToBuilding(workBuilding)
//            Util.toggleTaskOnWorker(worker!!, workBuilding, "haul")
//        }else if(workerAmounts.getOrDefault("produce", 99999)  == 0){
//            if(workerAmounts.getOrDefault("sell", 99999) == 0){
//                val worker = Util.createAndAssignWorkerToBuilding(workBuilding)
//                Util.toggleTaskOnWorker(worker!!, workBuilding, "produce", "sell")
//            }
//        }else if(workerAmounts.getOrDefault("pr"))
    }

    private fun getCurrentWorkerAmounts(workforce:WorkForceComponent):HashMap<String, NameAmountLink>{
        val map = hashMapOf<String, NameAmountLink>()
        val emptyArray = Array<Entity>() //Just make an empty array here for caching so we don't have to recreate one each loop
        for(i in 0 until workforce.workerTasksLimits.size){
            val limit = workforce.workerTasksLimits[i]
            val workers = workforce.workerTaskMap.getOrDefault(limit.taskName, emptyArray).size
            val maxWorkers = workforce.workerTasksLimits.firstOrNull { task -> task.taskName == limit.taskName }?.amount ?: 0
            map.put(limit.taskName, NameAmountLink(limit.taskName, workers, maxWorkers))
        }
        return map
    }

    private data class NameAmountLink(val name:String, val amount:Int, val max:Int)
}