package com.quickbite.economy.util

/**
 * Created by Paha on 1/31/2017.
 */
object TimeUtil {
    val timeScaleSpeeds = listOf(0f, 0.25f, 0.5f, 1f, 2f, 3f, 4f)
    var timeScaleSpeedIndex = 3
    var deltaTime:Float = 0f
    /**
     * A scale to apply to delta time.
     */
    var deltaTimeScale = 1f
    var paused = false

    /**
     * The calculated delta time using deltaTimeScale and deltaTime. Accounts for pausing
     */
    val scaledDeltaTime:Float
        get() = if(!paused) deltaTime * deltaTimeScale else 0f
}