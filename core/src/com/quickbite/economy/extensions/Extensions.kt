package com.quickbite.economy.extensions

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

/**
 * Created by Paha on 12/14/2016.
 */

val tmpVec1 = Vector2()

/**
 * Moves towards an X,Y position using a stepsize
 * @param x The X position
 * @param y The Y position
 * @param stepSize The step size to use for movement
 * @return A reused Vector2 that holds the new position after stepping towards it
 */
fun Vector2.moveTowards(x:Float, y:Float, stepSize:Float):Vector2{
    val angle = MathUtils.atan2(y- this.y, x - this.x)
    val xMove = MathUtils.cos(angle)*stepSize
    val yMove = MathUtils.sin(angle)*stepSize

    tmpVec1.set(xMove, yMove)

    this.set(this.x + xMove, this.y + yMove)

    return tmpVec1
}

/**
 * Moves towards a position using a step size
 * @param position The position to move towards
 * @param stepSize The size of the step to use
 * @return A reused Vector2 that holds the new position after stepping towards it
 */
fun Vector2.moveTowards(position:Vector2, stepSize:Float):Vector2{
    return this.moveTowards(position.x, position.y, stepSize)
}