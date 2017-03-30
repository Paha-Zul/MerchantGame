package com.quickbite.economy.util

/**
 * Created by Paha on 3/16/2017.
 */
object TimeOfDay{
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

    var hour = 0
    var minute = 0
    var second = 0

    var lastHour = hour
    var lastMinute = minute
    var lastSecond = second

    private fun set():TimeOfDay{
        //Save the previous
        lastHour = this.hour
        lastMinute = this.minute
        lastSecond = this.second

        //Record the new
        this.hour = (((counter/60) + 1)%25).toInt()
        this.minute = (counter%60).toInt()
        this.second = ((counter*60)%60).toInt()

        //Return ourselves
        return this
    }

    fun update(delta:Float){
        TimeOfDay.counter = (TimeOfDay.counter + delta * TimeOfDay.scale * TimeUtil.deltaTimeScale) % 1440
    }

    override fun toString(): String {
        return "$hour:$minute"
    }
}