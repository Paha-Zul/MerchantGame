package com.quickbite.economy.util

import com.badlogic.gdx.math.MathUtils

/**
 * Created by Paha on 2/7/2016.
 * Creates the timer as one time.
 * @param oneShot If the timer is a one shot timer (doesn't fire more than once)
 * @param callback The callback to be fired when the timer expires
 */
class CustomTimer(private var secondsDelay:Float, private var seconds: Float, var oneShot:Boolean = false, private var callback: (() -> Unit)? = null){

    /** Data to hold*/
    var userData:Any? = null

    var initialDelay = secondsDelay

    /** If the timer is done. */
    val done:Boolean
        get() = if(initialDelay < 0) currTime >= seconds else currTime >= initialDelay


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
        stopped = false
    }

    /**
     * Restarts the timer with the optional settings.
     * @param secondsDelay The initial delay of the timer. This defaults to 0 as to not be triggered again. If set above
     * 0, the intial delay will be used again.
     * @param seconds The time in seconds to run this timer
     */
    fun restart(secondsDelay: Float = 0f, seconds: Float = this.seconds){
        currTime = 0f
        this.seconds = seconds
        this.initialDelay = secondsDelay
        start()
    }
}