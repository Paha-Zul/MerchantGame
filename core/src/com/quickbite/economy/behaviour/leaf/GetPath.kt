package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.MyGame
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Pathfinder

/**
 * Created by Paha on 1/16/2017.
 */
class GetPath(bb:BlackBoard) : LeafTask(bb) {

    override fun start() {
        val transform = Mappers.transform.get(bb.myself)

        bb.path = Pathfinder.findPath(MyGame.grid, Vector2(transform.position), Vector2(bb.targetPosition))

        controller.finishWithSuccess()
    }
}