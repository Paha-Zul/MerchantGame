package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

/**
 * Created by Paha on 12/13/2016.
 */
class Grid(val squareSize:Int, val gridWidth:Int, val gridHeight:Int) {
    val grid:Array<Array<GridNode>>
    val offsetX = gridWidth*0.5f
    val offsetY = gridHeight*0.5f

    val offX = (offsetX/squareSize).toInt()
    val offY = (offsetY/squareSize).toInt()

    init{
        val rows = gridWidth/squareSize
        val cols = gridHeight/squareSize

        grid = Array(rows, {row -> Array(cols, {col -> GridNode(row, col)})})
    }

    fun setBlocked(centerX:Float, centerY:Float, halfWidth:Float, halfHeight:Float){
        forNodesInSquare(centerX, centerY, halfWidth + squareSize*0.49f, halfHeight + squareSize*0.49f, {node -> node.blocked = true})
    }

    fun setUnblocked(centerX:Float, centerY:Float, halfWidth:Float, halfHeight:Float){
        forNodesInSquare(centerX, centerY, halfWidth, halfHeight, {node -> node.blocked = false})
    }

    fun addEntity(x:Float, y:Float, entity: Entity, width:Int = 0, height:Int = 0){
        val index = getIndexOfGrid(x, y)
        if(width != 0 || height != 0){
            grid[index.first][index.second].entityList.add(entity)
        }else
            grid[index.first][index.second].entityList.add(entity)
    }

    fun addEntity(node:GridNode, entity:Entity){
        node.entityList.add(entity)
    }

    fun removeEntity(x:Float, y:Float, entity:Entity, width:Int = 0, height:Int = 0){
        val index = getIndexOfGrid(x, y)
        if(width != 0 || height != 0){
            grid[index.first][index.second].entityList.add(entity)
        }else
            grid[index.first][index.second].entityList.removeValue(entity, true)
    }

    fun removeEntity(node:GridNode, entity:Entity){
        node.entityList.removeValue(entity, true)
    }

    fun getNodeAtPosition(x:Float, y:Float):GridNode?{
        val index = getIndexOfGrid(x, y)
        if(index.first < 0 || index.first >= grid.size || index.second < 0 || index.second >= grid[index.first].size)
            return null

        return grid[index.first][index.second]
    }

    fun getNodeAtPosition(position: Vector2):GridNode?{
        return getNodeAtPosition(position.x, position.y)
    }

    fun getNodeAtIndex(x:Int, y:Int):GridNode?{
        if(x< 0 || x >= grid.size || y < 0 || y >= grid[x].size)
            return null

        return grid[x][y]
    }

    fun getIndexOfGrid(x:Float, y:Float):Pair<Int,Int>{
        return Pair(Util.roundDown(x, squareSize)/squareSize + offX, Util.roundDown(y, squareSize)/squareSize + offY)
    }

    /**
     * Converts a float value to a grid index value. Doesn't account for the offset
     */
    fun convertToGrid(value:Float):Int{
        return Util.roundDown(value, squareSize)/squareSize
    }

    fun convertToGrid(value:Int):Int{
        return value/squareSize
    }

    fun getNeighborsOf(x:Float, y:Float):List<GridNode>{
        val index = getIndexOfGrid(x, y)
        return getNeighborsOf(index.first, index.second)
    }
    
    fun getNeighborsOf(x:Int, y:Int):List<GridNode>{
        val list = mutableListOf<GridNode>()

        val upperX = Math.min(x + 1, grid.size - 1)
        val lowerX = Math.max(x - 1, 0)
        val upperY = Math.min(y + 1, grid[x].size - 1)
        val lowerY = Math.max(y - 1, 0)

        for(x1 in lowerX..upperX){
            for(y1 in lowerY..upperY){
                if(!(y1 == y && x1 == x))
                    list += getNodeAtIndex(x1, y1)!!
            }
        }

        return list.toList()
    }

    /**
     * Checks if any grid squares are blocked for the parameters...
     */
    fun isBlocked(centerX:Float, centerY:Float, halfWidth:Float, halfHeight: Float):Boolean{
        var blocked = false
        forNodesInSquare(centerX, centerY, halfWidth, halfHeight, {node ->
            if(node.blocked)
                blocked = true
        })

        return blocked
    }

    fun forNodesInSquare(centerX:Float, centerY:Float, halfWidth:Float, halfHeight:Float, func:(GridNode)->Unit){
        val index = getIndexOfGrid(centerX, centerY)

        val width = (halfWidth/squareSize).toInt()
        val height = (halfHeight/squareSize).toInt()

        val upperX = Math.min(index.first + width, grid.size - 1)
        val lowerX = Math.max(index.first - width, 0)
        val upperY = Math.min(index.second + height, grid[index.first].size - 1)
        val lowerY = Math.max(index.second - height, 0)

        for(x1 in lowerX..upperX){
            for(y1 in lowerY..upperY){
                func(getNodeAtIndex(x1, y1)!!)
            }
        }
    }

    fun debugDrawGrid(renderer:ShapeRenderer){
        renderer.color = Color.BLACK
        for(x in 0..grid.size-1){
            for(y in 0..grid[x].size-1){
                renderer.rect(grid[x][y].xPos - squareSize*0.5f, grid[x][y].yPos - squareSize*0.5f, squareSize.toFloat(), squareSize.toFloat())
            }
        }
    }

    fun debugDrawObstacles(renderer:ShapeRenderer){
        renderer.color = Color.RED
        for(x in 0..grid.size-1){
            for(y in 0..grid[x].size-1){
                if(grid[x][y].blocked)
                    renderer.rect(grid[x][y].xPos - squareSize*0.5f, grid[x][y].yPos - squareSize*0.5f, squareSize.toFloat(), squareSize.toFloat())
            }
        }
    }

    inner class GridNode(val x:Int, val y:Int){
        val entityList = com.badlogic.gdx.utils.Array<Entity>()
        var blocked = false

        /**
         * The X position of this node (not centered)
         */
        val xPos:Float
            get() = ((x - offX)*squareSize).toFloat()

        /**
         * The Y position of this node (not centered)
         */
        val yPos:Float
            get() = ((y - offY)*squareSize).toFloat()

        override fun toString(): String {
            return "[$x, $y]"
        }
    }
}