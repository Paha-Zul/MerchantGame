package com.quickbite.economy.levels

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ItemAmountChangeEvent
import com.quickbite.economy.managers.TownManager
import com.quickbite.economy.objects.Town
import com.quickbite.economy.util.Factory
import com.quickbite.economy.util.TownItemIncome
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 6/4/2017.
 */
object Level1 {
    fun  start(){
        setupTown()

        //TODO Make sure to change this to wherever on the map we need when we get a moveable map
        //Create a shop in the center of the map
        val shop = Factory.createObjectFromJson("shop", Vector2(0f, 0f))!!
        val shopWorker = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        Util.assignWorkerToBuilding(shopWorker, shop)
        Util.toggleTaskOnWorker(shopWorker, shop, "sell")

        Util.addImportItemToEntityReselling(TownManager.getTown("Town").itemImportMap["milk"]!!, shop, "Town")

        val farm = Factory.createObjectFromJson("farm", Vector2(-200f, 0f))!!
        val farmWorker = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        Util.assignWorkerToBuilding(farmWorker, farm)

        val flourMill = Factory.createObjectFromJson("flour mill", Vector2(0f, -200f))!!
        val flourHauler = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        val flourProducer = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        Util.assignWorkerToBuilding(flourHauler, flourMill)
        Util.assignWorkerToBuilding(flourProducer, flourMill)
        Util.toggleTaskOnWorker(flourHauler, flourMill, "haul")
        Util.toggleTaskOnWorker(flourProducer, flourMill, "produce")
        Util.toggleTaskOnWorker(flourProducer, flourMill, "sell")

        val bakery = Factory.createObjectFromJson("bakery", Vector2(-200f, -200f))!!
        val bakeryHauler = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        val bakeryHauler2 = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        val bakeryProducer = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        Util.assignWorkerToBuilding(bakeryHauler, bakery)
        Util.assignWorkerToBuilding(bakeryHauler2, bakery)
        Util.assignWorkerToBuilding(bakeryProducer, bakery)
        Util.toggleTaskOnWorker(bakeryHauler, bakery, "haul")
        Util.toggleTaskOnWorker(bakeryHauler2, bakery, "haul")
        Util.toggleTaskOnWorker(bakeryProducer, bakery, "produce")
        Util.toggleTaskOnWorker(bakeryProducer, bakery, "sell")

        makeTableShop(Vector2(-250f, 300f))
        makeTableShop(Vector2(0f, 300f))
        makeTableShop(Vector2(250f, 300f))
        makeTableShop(Vector2(500f, 300f))

        makeTreeFeller(Vector2(400f, -100f))

        makeLumberyard(Vector2(200f, -200f))
        makeLumberyard(Vector2(200f, 0f))
    }

    private fun setupTown(){
        val town = Town("Town")
        town.itemImportMap.put("wheat", TownItemIncome("wheat", 75))
        town.itemImportMap.put("milk", TownItemIncome("milk", 75))
        town.itemImportMap.put("wood log", TownItemIncome("wood log", 20))
        town.population = 200f

        //This is fired every time an item is added or removed from an inventory
        GameEventSystem.subscribe<ItemAmountChangeEvent> { (name, amount) ->
            town.totalSellingItemMap.compute(name, { _, result -> if(result == null) amount else result + amount})
        }

        TownManager.addTown("Town", town)
    }

    private fun makeTableShop(position:Vector2): Entity {
        val tableShop = Factory.createObjectFromJson("Table Shop", position)!!
        val worker1 = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        val worker2 = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        val worker3 = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        val worker4 = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        Util.assignWorkerToBuilding(worker1, tableShop)
        Util.assignWorkerToBuilding(worker2, tableShop)
        Util.assignWorkerToBuilding(worker3, tableShop)
        Util.assignWorkerToBuilding(worker4, tableShop)
        Util.toggleTaskOnWorker(worker1, tableShop, "haul")
        Util.toggleTaskOnWorker(worker2, tableShop, "haul")
        Util.toggleTaskOnWorker(worker3, tableShop, "haul")
        Util.toggleTaskOnWorker(worker4, tableShop, "sell", "produce")

        return tableShop
    }

    private fun makeTreeFeller(position:Vector2): Entity {
        val treeFeller = Factory.createObjectFromJson("tree feller", position)!!
        val worker1 = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        val worker2 = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        val worker3 = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        Util.assignWorkerToBuilding(worker1, treeFeller)
        Util.assignWorkerToBuilding(worker2, treeFeller)
        Util.assignWorkerToBuilding(worker3, treeFeller)

        Factory.createObjectFromJson("tree", Vector2(position.x - 50, position.y + 75))
        Factory.createObjectFromJson("tree", Vector2(position.x - 25, position.y + 75))
        Factory.createObjectFromJson("tree", Vector2(position.x + 0, position.y + 75))
        Factory.createObjectFromJson("tree", Vector2(position.x + 25, position.y + 75))

        return treeFeller
    }

    private fun makeLumberyard(position:Vector2): Entity {
        val lumberyard = Factory.createObjectFromJson("lumberyard", position)!!
        val worker1 = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        val worker2 = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        val worker3 = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        val worker4 = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
        Util.assignWorkerToBuilding(worker1, lumberyard)
        Util.assignWorkerToBuilding(worker2, lumberyard)
        Util.assignWorkerToBuilding(worker3, lumberyard)
        Util.assignWorkerToBuilding(worker4, lumberyard)
        Util.toggleTaskOnWorker(worker1, lumberyard, "haul")
        Util.toggleTaskOnWorker(worker2, lumberyard, "haul")
        Util.toggleTaskOnWorker(worker3, lumberyard, "haul")
        Util.toggleTaskOnWorker(worker4, lumberyard, "sell", "produce")

        return lumberyard
    }
}