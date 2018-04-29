package com.quickbite.economy.util

import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.MyGame
import sun.util.resources.OpenListResourceBundle
import java.util.*
import kotlin.collections.HashMap

object Pathfinder2 {

    private val heuristicWeight = 2

    fun findPath(start:Grid.GridNode, goal:Grid.GridNode):List<Vector2>{

        val closedSet = hashSetOf<Grid.GridNode>()
        val cameFrom = hashMapOf<Grid.GridNode, Grid.GridNode>()
        val gScore = hashMapOf<Grid.GridNode, Int>()
        val fScore = hashMapOf<Grid.GridNode, Int>()
        val openSet = PriorityQueue<Grid.GridNode>(Comparator<Grid.GridNode> { o1, o2 ->
            val result = fScore[o1]!! - fScore[o2]!! //Compare based on FScore
            when {
                result < 0 -> -1
                result == 0 -> 0
                else -> 1
            }})

        openSet.add(start)
        gScore[start] = 0

        while(openSet.isNotEmpty()) {
            val node = openSet.poll()
            closedSet.add(node)

            //First check if the node is our goal...
            if (node.x == goal.x && node.y == goal.y) {
                //TODO Need to calculate the path here
                return reconstructPath(cameFrom, goal)
            }
            //Get the surrounding neighbors of the node
            val neighbors = MyGame.grid.getNeighborsOf(node.x, node.y)
            for(neighbor in neighbors){ //For each neighbor..
                //If it's in the closed set, simply continue
                if(closedSet.contains(neighbor) || node.blocked)
                    continue

                //Calculate the tent G score using the node's G score and the distance bwteen the two
                val tentGScore = gScore[node]!! + distanceBetween(node, neighbor)
                //If the neighbor doesn't have a Gscore, give it the max!
                if(!gScore.containsKey(neighbor))
                    gScore[neighbor] = Int.MAX_VALUE
                //If the tentGScore is greater than the neighbors Gscore, skip!
                if(tentGScore >= gScore[neighbor]!!)
                    continue

                cameFrom[neighbor] = node //Assign where the neighbor came from
                gScore[neighbor] = tentGScore //Assign the neighbors Gscore
                val estFScore = gScore[neighbor]!! + getH(node, neighbor) //Calculate the f score
                fScore[neighbor] = estFScore //Assign the neighbors fscore

                if(!openSet.contains(neighbor))
                    openSet.add(neighbor)
            }
        }

        return listOf()
    }

    private fun distanceBetween(node1:Grid.GridNode, node2:Grid.GridNode):Int{
        if(node1.x == node2.x || node1.y == node2.y)
            return 10 //Return 10 because it is on one of the sides

        return 14 //Return 14 here cause it's a diagonal
    }

    private fun getH(currNode:Grid.GridNode, endNode:Grid.GridNode):Int{
        val d1 = 10
        val d2 = 14
        //Make sure to remember to weigh these since we're using 10 and 14 instead of 1 and 1.4!!!!
        val dx = Math.abs(currNode.x - endNode.x)*heuristicWeight
        val dy = Math.abs(currNode.y - endNode.y)*heuristicWeight
        return d1 * (dx + dy) + (d2 - 2*d1) * Math.min(dx, dy)
    }

    private fun reconstructPath(cameFrom:HashMap<Grid.GridNode, Grid.GridNode>, goal:Grid.GridNode):List<Vector2>{
        var current = goal
        val list = mutableListOf(Vector2(current.xCenter, current.yCenter))

        while(cameFrom.containsKey(current)){
            current = cameFrom[current]!!
            list += Vector2(current.xCenter, current.yCenter)
        }

        return list.reversed()
    }
}