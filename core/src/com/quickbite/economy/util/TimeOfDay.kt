package com.quickbite.economy.util

import com.badlogic.gdx.utils.Array

/**
 * Created by Paha on 3/16/2017.
 */
object TimeOfDay{
    val hourlyListeners:Array<(Int) -> Unit> = Array()

    private var counter = 0f
        get
        set(value) {
            field = value
            set()
        }

    val scale = 10

    /** Takes into account the scale of delta time from TimeUtil. This is the calculated scale of time progression*/
    val currTimeScale:Int
        get() = scale*TimeUtil.deltaTimeScale

    var day = 0
    var hour = 0
    var minute = 0
    var second = 0

    var lastHour = hour
    var lastMinute = minute
    var lastSecond = second

    private fun set():TimeOfDay{
        if(lastHour != this.hour)
            hourlyListeners.forEach { it(this.hour) }

        //Save the previous
        lastHour = this.hour
        lastMinute = this.minute
        lastSecond = this.second

        //TODO Maybe make day a separate counter not tied to the ever growing counter?

        //Record the new
        this.day = (counter/1440).toInt()
        this.hour = (((counter/60))%24).toInt()
        this.minute = (counter%60).toInt()
        this.second = ((counter*60)%60).toInt()

        //Return ourselves
        return this
    }

    fun update(delta:Float){
        TimeOfDay.counter = (TimeOfDay.counter + delta * TimeOfDay.scale * TimeUtil.deltaTimeScale)

        //The %1440 keeps it from going forever up
//        TimeOfDay.counter = (TimeOfDay.counter + delta * TimeOfDay.scale * TimeUtil.deltaTimeScale)%1440
    }

    override fun toString(): String {
        return "$hour:$minute"
    }
}