package com.quickbite.economy.objects

import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.util.TimeOfDay
import com.quickbite.economy.util.TownItemIncome

/**
 * Created by Paha on 3/18/2017.
 */
class Town {
    private val updateTime = 1f
    private var updateCounter = 0f
    private var _c = 0

    var population:Int = 0
    val itemIncomeMap = hashMapOf<String, TownItemIncome>()

    var needsRating:Int = 500
        set(value){
            field = MathUtils.clamp(value, 0, 1000)
        }

    var luxuryRating:Int = 500
        set(value){
            field = MathUtils.clamp(value, 0, 1000)
        }

    fun update(delta:Float){
        updateCounter += delta

        if(updateCounter >= updateTime){
            updateCounter -= updateTime
            _c++

            itemIncomeMap.values.forEach { income ->
                //1440 is how many minutes in 24 hours
                income.accumulatedItemCounter = income.accumulatedItemCounter + (income.calculatedProductionAmtPerDay/(1440f/(TimeOfDay.currTimeScale)))
            }
        }
    }
}