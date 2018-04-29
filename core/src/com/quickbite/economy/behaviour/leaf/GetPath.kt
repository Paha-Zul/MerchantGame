package com.quickbite.economy.behaviour.leaf

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.MyGame
import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.components.TransformComponent
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Pathfinder
import com.quickbite.economy.util.Pathfinder2
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

/**
 * Created by Paha on 1/16/2017.
 *
 * Calculates and stores the path to the current bb.targetPosition
 */
class GetPath(bb:BlackBoard) : LeafTask(bb) {
    val transform:TransformComponent by lazy {  Mappers.transform.get(bb.myself) }

    override fun start() {
//        async(CommonPool) {
//            val deferred = async(CommonPool) {
//                Pathfinder.findPath(MyGame.grid, Vector2(transform.position), Vector2(bb.targetPosition))
//            }
//
//            bb.path = deferred.await()
//            controller.finishWithSuccess()
//        }

        bb.path = Pathfinder2.findPath(MyGame.grid.getNodeAtPosition(transform.position)!!, MyGame.grid.getNodeAtPosition(bb.targetPosition)!!)
        controller.finishWithSuccess()
    }
}