package com.quickbite.economy.objects

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

/**
 * Created by Paha on 7/14/2017.
 */
data class FarmObject(val position:Vector2, var plantProgress:Float, var sprite: Sprite)