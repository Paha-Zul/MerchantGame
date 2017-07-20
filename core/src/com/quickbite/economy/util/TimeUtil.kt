package com.quickbite.economy.util

/**
 * Created by Paha on 1/31/2017.
 */
object TimeUtil {
    var deltaTime:Float = 0f
    var deltaTimeScale = 1
    var paused = false

    val scaledDeltaTime:Float
        get() = if(!paused) deltaTime * deltaTimeScale else 0f
}