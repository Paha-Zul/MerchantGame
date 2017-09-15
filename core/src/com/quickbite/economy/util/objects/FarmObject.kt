package com.quickbite.economy.util.objects

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

/**
 * Created by Paha on 7/14/2017.
 * A class that holds information about a farm object
 * @param position The position of the object
 * @param plantProgress The progress (from 0 to 1) of the growth of the plant
 * @param sprite The graphic of the plant
 */
class FarmObject(val position:Vector2, var plantProgress:Float, var sprite: Sprite){
    var needsTending = false
    var readyToHarvest = false
    var reseved = false
}