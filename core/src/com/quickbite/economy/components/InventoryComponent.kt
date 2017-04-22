package com.quickbite.economy.components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.util.InventoryChangeListener
import com.quickbite.economy.util.ItemAmountLink

/**
 * Created by Paha on 12/14/2016.
 */
class InventoryComponent : MyComponent {
    val itemMap = hashMapOf<String, ItemAmountLink>()

    val outputItems = hashSetOf("All")
    val inputItems = hashSetOf<String>()

    /** A list of listeners for inventory changes.*/
    val inventoryChangeListeners:Array<(InventoryChangeListener)->Unit> = Array()

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

        inventoryChangeListeners.forEach { it.invoke(InventoryChangeListener(name, amount, item.itemAmount)) }

        return amount
    }

    /**
     * @param name The name of the item to remove
     * @param amount The amount to remove. -1 indicates all of the item.
     * @return The amount that was removed
     */
    fun removeItem(name:String, amount:Int = 1):Int{
        var amount = amount //This makes is mutable
        if(amount < 0)
            amount = getItemAmount(name)

        val item = itemMap[name]
        if(item != null){
            val amountTaken = if(item.itemAmount - amount < 0) item.itemAmount else amount

            item.itemAmount -= amount
            if(item.itemAmount <= 0) {
                item.itemAmount = 0
                itemMap.remove(name)
            }

            inventoryChangeListeners.forEach { it.invoke(InventoryChangeListener(name, amountTaken, item.itemAmount)) }

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