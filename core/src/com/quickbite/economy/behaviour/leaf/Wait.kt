package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask

/**
 * Created by Paha on 1/17/2017.
 * A behaviour to wait some amount of time (2 seconds by default)
 */
class Wait(bb:BlackBoard, val minWaitTime:Float = 2f, val maxWaitTime:Float = minWaitTime) : LeafTask(bb) {
    var counter = 0f
    var waitTime = 0f

    override fun start() {
        waitTime = MathUtils.random(minWaitTime, maxWaitTime)
    }

    override fun update(delta: Float) {
        super.update(delta)

        counter += delta
        if(counter >= waitTime)
            this.controller.finishWithSuccess()
    }

    override fun reset() {
        counter = 0f
    }
}