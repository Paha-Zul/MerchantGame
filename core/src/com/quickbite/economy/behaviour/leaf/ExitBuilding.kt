package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.BodyComponent
import com.quickbite.economy.components.GraphicComponent
import com.quickbite.economy.components.TransformComponent
import com.quickbite.economy.util.Constants
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 6/25/2017.
 *
 * Leaves a building, which will fade the Entity back in and move to the entrance of the building.
 *
 * If not inside a building or for some reason something goes wrong, this task will simply unhide the Entity's sprite
 * and succeed
 */
class ExitBuilding(bb:BlackBoard) : LeafTask(bb){
    var moveTime = 0.5f
    var fadeTime = 0.3f
    var fadeDelay = 0.2f
    var counter = 0f

    val gc : GraphicComponent by lazy { Mappers.graphic[bb.myself] }
    val tc : TransformComponent by lazy { Mappers.transform[bb.myself] }
    val vc by lazy { Mappers.velocity[bb.myself] }
    var targetTC : TransformComponent? = null
    val destPos = Vector2()
    val bc : BodyComponent by lazy { Mappers.body[bb.myself] }
    val startPos = Vector2()

    override fun start() {
        super.start()

        //If the target's transform is null (meaning we don't have a target) or it doesn't have an entrance, skip the fancy crap and unhide
        if(bb.insideEntity == null){
            finish()
            return
        }

        //If the target's transform is null or doesn't have any entrance's, simply finish
        targetTC = Mappers.transform[bb.insideEntity]
        if(targetTC == null || targetTC!!.spotMap["entrance"] == null){
            finish()
            return
        }

        //Scale the start position to Box2D coords
        startPos.set(tc.position.x* Constants.BOX2D_SCALE, tc.position.y* Constants.BOX2D_SCALE)

        //Get the entrance of the building and scale it to Box2D coords
        val spot = targetTC!!.spotMap["entrance"]!![0]
        destPos.set(targetTC!!.position.x + spot.x, targetTC!!.position.y + spot.y)
        destPos.set(destPos.x*Constants.BOX2D_SCALE, destPos.y*Constants.BOX2D_SCALE)

        gc.sprite.setAlpha(0f) //Initially set this to 0

        counter = 0f //We need to make sure this cleared for resets
        moveTime = startPos.dst(destPos)/(vc.baseSpeed*Constants.BOX2D_SCALE)
        fadeTime = moveTime*0.3f
        fadeDelay = moveTime*0.7f
    }

    override fun update(delta: Float) {
        super.update(delta)

        counter += delta
        val moveAlpha = MathUtils.clamp(counter/ moveTime, 0f, 1f) //The alpha for moving
        val fadeAlpha = MathUtils.clamp((counter - fadeDelay)/ fadeTime, 0f, 1f) //The alpha for fading

        //Move the body and set the alpha's sprite
        bc.body!!.setTransform(Vector2(MathUtils.lerp(startPos.x, destPos.x, moveAlpha), MathUtils.lerp(startPos.y, destPos.y, moveAlpha)), 0f)
        gc.sprite.setAlpha(fadeAlpha)

        if(counter >= moveTime){
            gc.hide = false
            gc.sprite.setAlpha(1f)
            //When we are done with the counter, finish!
            finish()
        }
    }

    private fun finish(){
        //Set all the stuff to unhide bb.myself
        gc.sprite.setAlpha(1f)
        gc.hide = false
        bb.insideEntity = null
        controller.finishWithSuccess()
    }
}