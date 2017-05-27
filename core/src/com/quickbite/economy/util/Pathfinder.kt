package com.quickbite.economy.util

import com.badlogic.gdx.math.Vector2
import java.util.*

/**
 * Created by Paha on 12/13/2016.
 */
object Pathfinder {

    fun findPath(grid:Grid, start:Grid.GridNode, end:Grid.GridNode):List<Vector2> {
        val closedSet = hashSetOf<Grid.GridNode>()
        val cameFrom = hashMapOf<Grid.GridNode, Grid.GridNode>()

        //First Int is GScore, second Int is FScore
        //GScore is distance to neighbor plus last gScore. FScore includes heuristic
        val scores = hashMapOf(Pair(start, Score(0, getH(start, end))))

        //The open set is the sorted priority queue. It's sorted based on FScore
        val openSet = PriorityQueue<Grid.GridNode>(10, Comparator<Grid.GridNode> { o1, o2 ->
            val result = scores[o1]!!.FScore - scores[o2]!!.FScore //Compare based on FScore
            if(result < 0)
                -1
            else if(result == 0)
                0
            else
                1
        })

        //Add the initial starting node
        openSet.add(start)

        //The current node...
        var current:Grid.GridNode

        //While we still have something in the open set...
        while(openSet.isNotEmpty()){
            current = openSet.poll() //Get the head

            //Break if we're at the end!
            if(current == end){
                break
            }

            //Add the current to the closed set, we don't want to check it again!
            closedSet.add(current)
            //Get the neighbors
            val neighbors = grid.getNeighborsOf(current.x, current.y)
            //For each neighbor...
            for(neighbor in neighbors){
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

                }else if(tentativeGScore >= neighborScore.GScore)
                    continue //Continue because the neighbor is not a good pick

                cameFrom[neighbor] = current //Assign the neighbors parent to the current node
                neighborScore.GScore = tentativeGScore //Set the neighbors G score as the tentative GScore
                neighborScore.FScore = neighborScore.GScore + getH(neighbor, end) //Set it's F Score as it's GScore + heuristic

                if(flag)
                    openSet.add(neighbor) //The scores need to be set before adding to the set
            }
        }

        current = end
        val path = mutableListOf(Vector2(current.xPos, current.yPos))
        while(cameFrom.containsKey(current)){
            current = cameFrom[current]!!
            if(current == start) continue
            path += Vector2(current.xPos, current.yPos)
        }

        return path.toList().reversed()
    }

    fun findPath(grid:Grid, start:Vector2, end:Vector2):List<Vector2> {
        return findPath(grid, grid.getNodeAtPosition(start.x, start.y)!!, grid.getNodeAtPosition(end.x, end.y)!!)
    }

    private fun getH(currNode:Grid.GridNode, endNode:Grid.GridNode):Int{
        val xDis = Math.abs(currNode.x - endNode.x)
        val yDis = Math.abs(currNode.y - endNode.y)
        var H = 0
        if(xDis > yDis)
            H = 14*yDis + 10*(xDis - yDis)
        else
            H = 14*xDis + 10*(yDis - xDis)

        return H
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