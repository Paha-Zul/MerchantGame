package com.quickbite.economy.extensions

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

/**
 * Created by Paha on 12/14/2016.
 */

val tmpVec1 = Vector2()

fun Vector2.moveTowards(x:Float, y:Float, stepSize:Float):Vector2{
    val angle = MathUtils.atan2(y- this.y, x - this.x)
    val xMove = MathUtils.cos(angle)*stepSize
    val yMove = MathUtils.sin(angle)*stepSize

    tmpVec1.set(xMove, yMove)

    this.set(this.x + xMove, this.y + yMove)

    return tmpVec1
}

fun Vector2.moveTowards(position:Vector2, stepSize:Float):Vector2{
    return this.moveTowards(position.x, position.y, stepSize)
}