package com.quickbite.economy.util

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.behaviour.Tasks
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.managers.ItemDefManager
import com.quickbite.economy.managers.TownManager
import com.quickbite.economy.objects.Town

/**
 * Created by Paha on 1/22/2017.
 */
object Spawner {
    val town:Town by lazy { TownManager.getTown("Town") }
    var buyerCounter = 0f
    var haulerCounter = 0f

    val spawnPosition = Vector2(-500f, 0f)

    val spawnBuyerTimeRange = Vector2(1f, 10f)
    val spawnHaulerTimeRange = Vector2(5f, 20f)

    var nextBuyerSpawnTime = MathUtils.random(spawnBuyerTimeRange.x, spawnBuyerTimeRange.y)
    var nextHaulerSpawnTime = MathUtils.random(spawnHaulerTimeRange.x, spawnHaulerTimeRange.y)

    fun update(delta:Float){
        buyerCounter += delta
        haulerCounter += delta

        spawnBuyer()
        spawnHauler()
    }

    private fun spawnBuyer(){

        if(buyerCounter >= nextBuyerSpawnTime){
            val list = ItemDefManager.itemDefMap.values.toList() //Get the list of items
            val randomItem = list[MathUtils.random(list.size-1)] //Randomly pick an item
            val itemToBuy = ItemAmountLink(randomItem.itemName, MathUtils.random(1, 20)) //Get an item to buy

            //If we found a building that is selling it, spawn a buyer
            if(Util.getClosestSellingItem(spawnPosition, itemToBuy.itemName) != null){
                //Randomly assign an item and amount wanted
                val entity = Factory.createObjectFromJson("buyer", spawnPosition)
                val buying = Mappers.buyer[entity]
                val inventory = Mappers.inventory[entity]

                buying.buyList.add(itemToBuy)
                inventory.addItem("Gold", 1000)

                //Scan each item that we are buying and calculate necessity and luxury ratings
                buying.buyList.forEach { item ->
                    val itemDef = ItemDefManager.itemDefMap[item.itemName]!!
                    if(itemDef.category == "Food")
                        buying.needsSatisfactionRating -= item.itemAmount
                }

            }

            buyerCounter -= nextBuyerSpawnTime
            nextBuyerSpawnTime = MathUtils.random(spawnBuyerTimeRange.x, spawnBuyerTimeRange.y)
        }
    }

    private fun spawnHauler(){
        if(haulerCounter >= nextHaulerSpawnTime){
            val list = town.itemIncomeMap.values.toList()
            val randomItem = list[MathUtils.random(list.size - 1)]
            val itemToBuy = ItemAmountLink(randomItem.itemName, MathUtils.random(1, randomItem.accumulatedItemCounter.toInt()))
            randomItem.accumulatedItemCounter -= itemToBuy.itemAmount

            if(Util.getClosestBuildingType(spawnPosition, BuildingComponent.BuildingType.Stockpile) != null){
                //Randomly assign an item and amount wanted
                val entity = Factory.createObjectFromJson("hauler", spawnPosition)
                val inventory = Mappers.inventory[entity]
                val beh = Mappers.behaviour[entity]

                inventory.addItem(itemToBuy.itemName, itemToBuy.itemAmount)

                beh.currTask = Tasks.haulInventoryToStockpile(beh.blackBoard)

            }

            haulerCounter -= nextHaulerSpawnTime
            nextHaulerSpawnTime = MathUtils.random(spawnHaulerTimeRange.x, spawnHaulerTimeRange.y)
        }
    }
}