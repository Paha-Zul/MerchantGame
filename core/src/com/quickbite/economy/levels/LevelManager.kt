package com.quickbite.economy.levels

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ItemAmountChangeEvent
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.managers.TownManager
import com.quickbite.economy.objects.Town
import com.quickbite.economy.util.Factory
import com.quickbite.economy.util.Util

object LevelManager {
    fun loadLevel(levelName:String){
        loadLevel(DefinitionManager.levelDefMap[levelName]!!)
    }

    fun loadLevel(level:DefinitionManager.LevelDef){
        //Initialize the town
        initTown(level.townDef)

        level.buildings.forEach { buildingDef ->
            //Create the building
            val building = Factory.createObjectFromJson(buildingDef.buildingName, buildingDef.position)!!

            //Add the workers
            buildingDef.workers.forEach { tasks ->
                val worker = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
                Util.assignWorkerToBuilding(worker, building)
                Util.toggleTaskOnWorker(worker, building, *tasks)
            }

            //Add any imports we have from the town
            buildingDef.importing.forEach { Util.addImportItemToEntityReselling(TownManager.getTown("Town").itemImportMap[it]!!, building, "Town") }
        }
    }

    private fun initTown(townDef:DefinitionManager.LevelTownDef){
        val town = Town("Town")

        town.population = townDef.startingPop
        //TODO Why is productionPercentageModifier 0? It reads from TOML and breaks...
        //For some reason here, productionPercentageModifier starts at 0 which isn't right... we need to manually set it for now
        townDef.imports.forEach { import -> town.itemImportMap.put(import.itemName, import.apply { productionPercentageModifier = 1f }) }

        //Listens for changes in any inventory change event. Basically records the overall items in the town
        GameEventSystem.subscribe<ItemAmountChangeEvent> { (name, amount) ->
            town.totalSellingItemMap.compute(name, { _, result -> if(result == null) amount else result + amount})
        }

        TownManager.addTown("Town", town)
    }
}