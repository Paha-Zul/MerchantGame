package com.quickbite.economy.util

import com.badlogic.gdx.math.MathUtils

/**
 * Created by Paha on 2/7/2016.
 * Creates the timer as one time.
 * @param oneShot If the timer is a one shot timer (doesn't fire more than once)
 * @param _callback The callback to be
 */
class CustomTimer(private var seconds: Float, var oneShot:Boolean = false, private var callback: (() -> Unit)? = null){

    /** Data to hold*/
    var userData:Any? = null

    /** If the timer is done. */
    val done:Boolean
        get() = currTime >= seconds


    var stopped:Boolean = false
        get
        private set

    val remainingTime:Float
        get() {
            var remaining = seconds - currTime
            remaining = MathUtils.clamp(remaining, 0f, seconds)
            return remaining
        }

    private var currTime:Float = 0f

    fun update(delta:Float){
        //If we're not stopped...
        if(!stopped) {
            currTime += delta   //Increment timer
            if(done){     //Otherwise, if is done
                finish()        //Finish the timer.
            }
        }
    }

    /**
     * Finishes (and restarts if there is an interval) the timer when the timer expires.
     */
    private fun finish(){
        stop()
        callback?.invoke()
        if(seconds >= 0 && !oneShot)
            restart()
    }

    /**
     * Stops the timer.
     */
    fun stop() {
        stopped = true
    }

    /**
     * Starts the timer (if it hasn't expired)
     */
    fun start(){
        if(!done)
            stopped = false
    }

    /**
     * Restarts the timer with the optional settings.
     * @param seconds
     */
    fun restart(seconds: Float = this.seconds){
        currTime = 0f
        this.seconds = seconds
        start()
    }
}