package com.quickbite.economy.util

/**
 * Created by Paha on 3/18/2017.
 *
 * @param itemName The name of the item
 * @param baseProductionAmtPerDay The base amount the town produces per day
 */
class TownItemIncome(val itemName:String, var baseProductionAmtPerDay:Int) {

    /** The accumulated amount waiting to be processed (hauled to a stockpile?) This shouldn't be accessible until
     * given to the player in an accessible way (stockpile) */
    var accumulatedItemCounter:Float = 0f

    /** The percentage modifier of the item production.*/
    var productionPercentageModifier = 1f

    /** The base production times the percentage modifier */
    val calculatedProductionAmtPerDay:Int
        get() = (baseProductionAmtPerDay*productionPercentageModifier).toInt()
}