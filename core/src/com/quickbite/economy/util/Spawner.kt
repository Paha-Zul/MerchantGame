package com.quickbite.economy.util

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

/**
 * Created by Paha on 1/22/2017.
 */
object Spawner {
    var counter = 0f
    val spawnPosition = Vector2(-500f, 0f)
    val spawnRange = Vector2(1f, 5f)
    var nextSpawnTime = MathUtils.random(spawnRange.x, spawnRange.y)

    val demandList = listOf(ItemAmountLink("Wood Plank", 20), ItemAmountLink("Wood Table", 1))

    fun update(delta:Float){
        counter += delta

        if(counter >= nextSpawnTime){
            val randomItem = demandList[MathUtils.random(demandList.size - 1)]
            val itemToBuy = ItemAmountLink(randomItem.itemName, MathUtils.random(1, randomItem.itemAmount))
            if(Util.getClosestSellingItem(spawnPosition, itemToBuy.itemName) != null){
                //Randomly assign an item and amount wanted
                val entity = Factory.createObjectFromJson("buyer", spawnPosition)
                val buying = Mappers.buyer[entity]
                val inventory = Mappers.inventory[entity]

                buying.buyList.add(itemToBuy)
                inventory.addItem("Gold", MathUtils.random(100))
            }

            counter -= nextSpawnTime
            nextSpawnTime = MathUtils.random(spawnRange.x, spawnRange.y)
        }
    }
}