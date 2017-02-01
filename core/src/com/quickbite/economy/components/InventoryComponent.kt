package com.quickbite.economy.components

import com.quickbite.economy.interfaces.MyComponent

/**
 * Created by Paha on 12/14/2016.
 */
class InventoryComponent : MyComponent {
    val itemMap = hashMapOf<String, InventoryItem>()

    /**
     *
     * @param name The name of the item to add.
     * @param amount The amount to add.
     * @return The amount added.
     */
    fun addItem(name:String, amount:Int = 1):Int{
        val item = itemMap.getOrPut(name, { InventoryItem(name, 0) })
        item.amount += amount
        return amount
    }

    /**
     * @param name The name of the item to remove
     * @param amount The amount to remove
     * @return The amount that was removed
     */
    fun removeItem(name:String, amount:Int = 1):Int{
        val item = itemMap[name]
        if(item != null){
            val amountTaken = if(item.amount - amount < 0) item.amount else amount

            item.amount -= amount
            if(item.amount <= 0)
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
            return itemMap[name]!!.amount

        return 0
    }

    class InventoryItem(val name:String, var amount:Int){
        override fun toString(): String {
            return "[$name:$amount]"
        }
    }

    override fun dispose() {

    }

    override fun initialize() {

    }
}