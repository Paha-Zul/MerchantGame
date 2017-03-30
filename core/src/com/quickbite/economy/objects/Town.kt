package com.quickbite.economy.objects

import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.util.CustomTimer
import com.quickbite.economy.util.TimeOfDay
import com.quickbite.economy.util.TownItemIncome

/**
 * Created by Paha on 3/18/2017.
 */
class Town {
    private var _c = 0 //Temp counter to check if we're updating correctly

    var population:Int = 0
    val itemIncomeMap = hashMapOf<String, TownItemIncome>()

    val populationIncreaseFromRatingThreshold = 20

    var needsRating:Int = 500
        set(value){
            field = MathUtils.clamp(value, 0, 1000)
        }

    var luxuryRating:Int = 500
        set(value){
            field = MathUtils.clamp(value, 0, 1000)
        }

    val accumulateItemsTimer:CustomTimer
    val changePopulationTimer:CustomTimer

    init{
        //TODO Make sure this timer works
        accumulateItemsTimer = CustomTimer(1f, false, {
            _c++

            //For each item in the income map, increase it!
            itemIncomeMap.values.forEach { income ->
                //1440 is how many minutes in 24 hours
                income.accumulatedItemCounter = income.accumulatedItemCounter + (income.calculatedProductionAmtPerDay/(1440f/(TimeOfDay.currTimeScale)))
            }
        })

        changePopulationTimer = CustomTimer(10f, false, {
            val diff = needsRating - 500 //This will either be negative or positive
            val change = diff/populationIncreaseFromRatingThreshold
            population += change
            System.out.println("[Town] Population: $population")
        })
    }

    fun update(delta:Float){
        accumulateItemsTimer.update(delta)
        changePopulationTimer.update(delta)
    }
}