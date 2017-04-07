package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.util.ItemAmountLink

/**
 * Created by Paha on 12/14/2016.
 */
class InventoryComponent : MyComponent {
        val itemMap = hashMapOf<String, ItemAmountLink>()

    val outputItems = hashSetOf("All")
    val inputItems = hashSetOf<String>()

    /**
     * @param name The name of the item to add.
     * @param amount The amount to add.
     * @return The amount added.
     */
    fun addItem(name:String, amount:Int = 1):Int{
        if(amount < 1)
            return 0

        val item = itemMap.getOrPut(name, { ItemAmountLink(name, 0) })
        item.itemAmount += amount
        return amount
    }

//    /**
//     * Adds an item to this inventory
//     * @param itemName The itemName of the item to add.
//     * @param itemAmount The itemAmount to add.
//     * @return The itemAmount added.
//     */
//    fun addItem(itemName:String, itemAmount:Int = 1, producedAt:Entity? = null):Int{
//        if(itemAmount < 1)
//            return 0
//
//        val list = itemMap.getOrPut(itemName, { Array()})
//        var item = list.firstOrNull { it.itemName == itemName && it.producedAt === producedAt }
//        if(item == null){
//            item = Item(itemName, itemAmount, producedAt)
//            list.add(item)
//        }else{
//            item.itemAmount += itemAmount
//        }
//
//        return itemAmount
//    }
//
//    /**
//     * Adds an item to this inventory
//     * @param item The Item to add
//     * @return The item amount that was added.
//     */
//    fun addItem(item:Item):Int{
//        return addItem(item.itemName, item.itemAmount, item.producedAt)
//    }

    /**
     * @param name The name of the item to remove
     * @param amount The amount to remove. -1 indicates all of the item.
     * @return The amount that was removed
     */
    fun removeItem(name:String, amount:Int = 1):Int{
        var amount = amount
        if(amount < 0)
            amount = getItemAmount(name)

        val item = itemMap[name]
        if(item != null){
            val amountTaken = if(item.itemAmount - amount < 0) item.itemAmount else amount

            item.itemAmount -= amount
            if(item.itemAmount <= 0)
                itemMap.remove(name)

            return amountTaken
        }

        return 0
    }

    /**
     * @param name The name of the item
     * @return True if the item is in the inventory, false otherwise.
     */
    fun hasItem(name:String):Boolean{
        return itemMap.containsKey(name)
    }

    /**
     * Gets the item amount if available
     * @param name The name of the item
     * @return The amount of the item or 0 if the item doesn not exist.
     */
    fun getItemAmount(name:String):Int{
        if(hasItem(name))
            return itemMap[name]!!.itemAmount

        return 0
    }

    override fun dispose(entity: Entity) {

    }

    override fun initialize() {

    }
}