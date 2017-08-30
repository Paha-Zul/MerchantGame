package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.VelocityComponent
import com.quickbite.economy.extensions.moveTowards
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/16/2017.
 */
class MoveToPath(bb:BlackBoard) : LeafTask(bb) {
    var index = 0

    lateinit var position: Vector2
    var velocity:VelocityComponent? = null

    val tmp = Vector2()

    override fun start() {
        position = Mappers.transform.get(bb.myself).position
        velocity = Mappers.velocity.get(bb.myself)

        tmp.set(position)
    }

    override fun update(delta: Float) {
        super.update(delta)

        val speed = velocity!!.baseSpeed*delta

        //If the path is not empty, move!
        if(bb.path.isNotEmpty()){
            tmp.set(position) //Set the tmp vector. We don't want to directly change the position

            //TODO Can we clamp this velocity towards the next step so we don't overshoot it?
            //Set the velocity
            velocity!!.velocity.set(tmp.moveTowards(bb.path[index], speed))

            //If our unit's position is within the destination, move to the next path.
            if(this.position.dst(bb.path[index]) <= (speed*2f)){
                index++
                if(index >= bb.path.size)
                    bb.path = listOf() //Clear the itemPriceLinkList so we don't follow anything
            }
        }else
            this.controller.finishWithSuccess() //If the path is empty we are finished
    }

    override fun end() {
        super.end()

        velocity?.velocity?.set(0f, 0f)
    }

    override fun reset() {
        super.reset()
        index = 0
    }
}