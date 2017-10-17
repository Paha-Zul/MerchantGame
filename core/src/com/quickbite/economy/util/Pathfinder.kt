package com.quickbite.economy.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.components.DebugDrawComponent
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.yield
import java.util.*

/**
 * Created by Paha on 12/13/2016.
 */
object Pathfinder {
    private val delayTime:Long = 5

    private val green = Color(Color.GREEN).apply { a = 0.2f }
    private val blue = Color(Color.BLUE).apply { a = 0.2f }
    private val red = Color(Color.RED).apply { a = 0.2f }

    suspend fun findPath(grid:Grid, start:Grid.GridNode, end:Vector2):List<Vector2> {
        val closedSet = hashSetOf<Grid.GridNode>()

        /** The key is the 'to' or 'child'. The value is the 'from' or 'parent' **/
        val cameFrom = hashMapOf<Grid.GridNode, Grid.GridNode>()

        val endNode = grid.getNodeAtPosition(end.x, end.y)!!
        if(DebugDrawComponent.GLOBAL_DEBUG_PATHFINDING) endNode.color = Color.RED

        //First Int is GScore, second Int is FScore
        //GScore is distance to neighbor plus last gScore. FScore includes heuristic
        val scores = hashMapOf(Pair(start, Score(0, getH(start, endNode))))

        //The open set is the sorted priority queue. It's sorted based on FScore
        val openSet = PriorityQueue<Grid.GridNode>(10, Comparator<Grid.GridNode> { o1, o2 ->
            val result = scores[o1]!!.FScore - scores[o2]!!.FScore //Compare based on FScore
            when {
                result < 0 -> -1
                result == 0 -> 0
                else -> 1
            }
        })

        //Add the initial starting node
        openSet.add(start)

        //The current node...
        var current:Grid.GridNode

        //While we still have something in the open set...
        while(openSet.isNotEmpty()){
            current = openSet.poll() //Get the head
            if(DebugDrawComponent.GLOBAL_DEBUG_PATHFINDING) current.color = Color.YELLOW

            //Break if we're at the end!
            if(current == endNode){
                break
            }

            //Add the current to the closed set, we don't want to check it again!
            closedSet.add(current)
            //Get the neighbors
            val neighbors = grid.getNeighborsOf(current.x, current.y)
            //For each neighbor...
            for(neighbor in neighbors){
                if(DebugDrawComponent.GLOBAL_DEBUG_PATHFINDING){
                    neighbor.color = blue
                    delay(delayTime)
                    yield()
                }
                //If it's in the closed set or blocked, continue
                if(closedSet.contains(neighbor) || neighbor.blocked)
                    continue

                val currScore = scores[current]!! //Get the current score
                val neighborScore = scores.getOrPut(neighbor, {Score(Int.MAX_VALUE, Int.MAX_VALUE)})

                //Take the current node's GScore and add it to the new movement cost from
                val tentativeGScore = currScore.GScore + getDisCost(current, neighbor) //TODO MAKE THIS BETTER
                var flag = false

                if(!openSet.contains(neighbor)) {
                    flag = true

                }else if(tentativeGScore >= neighborScore.GScore) {
                    if(DebugDrawComponent.GLOBAL_DEBUG_PATHFINDING) neighbor.color = red
                    continue //Continue because the neighbor is not a good pick
                }

                cameFrom[neighbor] = current //Assign the neighbors parent to the current node
                neighborScore.GScore = tentativeGScore //Set the neighbors G score as the tentative GScore
                neighborScore.FScore = neighborScore.GScore + getH(neighbor, endNode) - neighbor.terrain!!.roadLevel*2 //Set it's F Score as it's GScore + heuristic

                neighbor.scores.first = neighborScore.GScore
                neighbor.scores.second = neighborScore.FScore

                if(flag)
                    openSet.add(neighbor) //The scores need to be set before adding to the set
            }
        }

        //This is where we trace backwards and make our final path
        val path = mutableListOf(Vector2(end.x, end.y))
        current = endNode
        if(DebugDrawComponent.GLOBAL_DEBUG_PATHFINDING) current.color = green
        while(cameFrom.containsKey(current)){
            current = cameFrom[current]!!
            if(DebugDrawComponent.GLOBAL_DEBUG_PATHFINDING) current.color = green
            if(current == start) continue
            path += Vector2(current.xCenter, current.yCenter)
            if(DebugDrawComponent.GLOBAL_DEBUG_PATHFINDING) {
                delay(delayTime)
                yield()
            }
        }

        closedSet.forEach { it.scores.apply { first = 0; second = 0 } }
        openSet.forEach { it.scores.apply { first = 0; second = 0 }  }
        cameFrom.forEach { from, to ->  from.scores.apply { first = 0; second = 0 }; to.scores.apply { first = 0; second = 0 } }

        return path.toList().reversed()
    }

    suspend fun findPath(grid:Grid, start:Vector2, end:Vector2):List<Vector2> {
        return findPath(grid, grid.getNodeAtPosition(start.x, start.y)!!, Vector2(end))
    }

    private fun getH(currNode:Grid.GridNode, endNode:Grid.GridNode):Int{
        val d1 = 10
        val d2 = 14
        //Make sure to remember to weigh these since we're using 10 and 14 instead of 1 and 1.4!!!!
        val dx = Math.abs(currNode.x - endNode.x)*10
        val dy = Math.abs(currNode.y - endNode.y)*10
        return d1 * (dx + dy) + (d2 - 2*d1) * Math.min(dx, dy)
    }

    private fun getDisCost(currNode:Grid.GridNode, targetNode:Grid.GridNode):Int{
        if(currNode.x == targetNode.x || currNode.y == targetNode.y)
            return 10 //Return 10 because it is on one of the sides

        return 14 //Return 14 here cause it's a diagonal
    }

    /**
     * @param GScore The movement cost from the start node to this point
     * @param FScore The combined cost of G plus a hueristic (H) score
     */
    private class Score(var GScore:Int, var FScore:Int){
        override fun toString(): String {
            return "[G:$GScore, F:$FScore]"
        }
    }

}