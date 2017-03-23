package com.quickbite.economy.util

/**
 * Created by Paha on 1/31/2017.
 */
object TimeUtil {
    var deltaTime:Float = 0f
    var deltaTimeScale = 1
    var pausedBonus = 1

    val scaledDeltaTime:Float
        get() = deltaTime * deltaTimeScale * pausedBonus
}