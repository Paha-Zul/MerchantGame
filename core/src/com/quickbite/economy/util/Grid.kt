package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.quickbite.economy.MyGame
import com.quickbite.economy.MyGame.renderer
import com.quickbite.economy.objects.Terrain
import com.quickbite.economy.util.objects.MutablePair

/**
 * Created by Paha on 12/13/2016.
 */
class Grid(val squareSize:Int, val gridWidth:Int, val gridHeight:Int) {
    val grid:Array<Array<GridNode>>
    private val offsetX = gridWidth*0.5f
    private val offsetY = gridHeight*0.5f

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

    fun setBlocked(centerX:Float, centerY:Float, halfWidth:Float, halfHeight:Float, gridSquaresToExclude:Array<Array<Int>>){
        val startNode = getNodeAtPosition(centerX, centerY)!!
        val startIndex = getIndexOfGrid(startNode.xCenter - halfWidth.toInt(), startNode.yCenter - halfHeight.toInt())

        forNodesInSquare(centerX, centerY, halfWidth + squareSize*0.49f, halfHeight + squareSize*0.49f,
            {node ->
                val adjustedXNode = node.x - startIndex.first
                val adjustedYNode = node.y - startIndex.second
                if(!gridSquaresToExclude.any { it[0] == adjustedXNode && it[1] == adjustedYNode })
                    node.blocked = true
            })
    }

    fun setBlocked(centerX:Float, centerY:Float, dimensionsToBlock:Array<Array<Int>>, gridSquaresToExclude:Array<Array<Int>>){
        val startNode = getNodeAtPosition(centerX, centerY)!!
        val centerIndex = Pair (startNode.x, startNode.y) //Used for calculating the offset
        val startIndex = Pair (startNode.x + dimensionsToBlock[0][0], startNode.y + dimensionsToBlock[0][1]) //This is where we begin (lower left)
        val endIndex = Pair (startNode.x + dimensionsToBlock[1][0], startNode.y + dimensionsToBlock[1][1]) //This is where we end (top right)

        forNodesInSquare(startIndex, endIndex,
                {node ->
                    //Here we take the current node X and Y and subtract it from the center. That gives us an 'adjusted' offset X and Y
                    val adjustedXNode = node.x - centerIndex.first
                    val adjustedYNode = node.y - centerIndex.second
                    if(!gridSquaresToExclude.any { it[0] == adjustedXNode && it[1] == adjustedYNode })
                        node.blocked = true
                })
    }

    /**
     * Sets an area to be unblocked on the grid
     * @param centerX The center X position
     * @param centerY The center Y position
     * @param halfWidth The half width of the area
     * @param halfHeight The half height of the area
     */
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

    /**
     * Gets a GridNode at a real world position
     * @param x The X world position
     * @param y The Y world position
     * @return A GridNode if the X,Y world position was valid, otherwise null
     */
    fun getNodeAtPosition(x:Float, y:Float):GridNode?{
        val index = getIndexOfGrid(x, y)
        if(index.first < 0 || index.first >= grid.size || index.second < 0 || index.second >= grid[index.first].size)
            return null

        return grid[index.first][index.second]
    }

    /**
     * Gets a GridNode at a real world position
     * @param position The real world position
     * @return A GridNode if the X,Y world position was valid, otherwise null
     */
    fun getNodeAtPosition(position: Vector2):GridNode?{
        return getNodeAtPosition(position.x, position.y)
    }

    /**
     * Gets a node at the index passed in
     * @param x The X index of the grid
     * @param y The Y index of the grid
     * @return A GridNode if the index was valid, otherwise null.
     */
    fun getNodeAtIndex(x:Int, y:Int):GridNode?{
        if(x< 0 || x >= grid.size || y < 0 || y >= grid[x].size)
            return null

        return grid[x][y]
    }

    /**
     * Gets the index of the grid using an X and Y float position
     * @param x The X world position
     * @param y The Y world position
     * @return A Pair that holds the X/Y index of the grid.
     */
    fun getIndexOfGrid(x:Float, y:Float):Pair<Int,Int>{
        return Pair(Util.roundDown(x + squareSize/2f, squareSize)/squareSize + offX, Util.roundDown(y + squareSize/2f, squareSize)/squareSize + offY)
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

    /**
     * Gets the 8 surrounding neighbors of the node at the X and Y index passed in
     * @param x The X index
     * @param y The Y index
     * @return A list of GridNode neighbors
     */
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

    fun forNodesInSquare(startIndex:Pair<Int, Int>, endIndex:Pair<Int, Int>, func:(GridNode)->Unit){
        for(x1 in startIndex.first..endIndex.first){
            for(y1 in startIndex.second..endIndex.second){
                func(getNodeAtIndex(x1, y1)!!)
            }
        }
    }

    fun debugDrawGrid(renderer:ShapeRenderer){
        renderer.color = Color.BLACK
        for(x in 0 until grid.size){
            for(y in 0 until grid[x].size){
                renderer.rect(grid[x][y].xPos, grid[x][y].yPos, squareSize.toFloat(), squareSize.toFloat())
            }
        }
    }

    fun debugDrawObstacles(renderer:ShapeRenderer){
        val grayColor = Color(0f, 0f, 0f, 0.4f)

        for(x in 0 until grid.size){
            for(y in 0 until grid[x].size){
                renderer.color = if(grid[x][y].blocked) grayColor
                else grid[x][y].color

                if(grid[x][y].blocked || grid[x][y].color != Color.WHITE)
                    renderer.rect(grid[x][y].xPos, grid[x][y].yPos, squareSize.toFloat(), squareSize.toFloat())
            }
        }
    }

    fun debugDrawGridScores(batch:SpriteBatch){
        batch.projectionMatrix = MyGame.camera.combined
        for(x in 0 until grid.size){
            for(y in 0 until grid[x].size){
                val node = grid[x][y]
                val worldCoords = Vector2(node.xPos, node.yPos + MyGame.grid.squareSize)
                MyGame.camera.viewportWidth
                MyGame.defaultFont8.draw(batch, "G:${node.scores.first} \n F:${node.scores.second}", worldCoords.x, worldCoords.y)
            }
        }
    }

    /**
     * A GridNode for a Grid
     * @param x The X index of the grid node. This is not the actual world position
     * @param y The Y index of the grid node. This is not the actual world position
     */
    inner class GridNode(val x:Int, val y:Int){
        val entityList = com.badlogic.gdx.utils.Array<Entity>()
        var blocked = false
        var terrain: Terrain? = null

        var color:Color = Color.WHITE
        var scores:MutablePair<Int, Int> = MutablePair(0, 0)

        /**
         * The X position of this node (not centered)
         */
        val xPos:Float
            get() = ((x - offX)*squareSize).toFloat() - squareSize/2f

        /**
         * The X position of this node (centered)
         */
        val xCenter:Float
            get() = (x - offX)*squareSize.toFloat()

        /**
         * The Y position of this node (centered)
         */
        val yCenter:Float
            get() = (y - offY)*squareSize.toFloat()

        /**
         * The Y position of this node (not centered)
         */
        val yPos:Float
            get() = ((y - offY)*squareSize).toFloat() - squareSize/2f

        override fun toString(): String {
            return "[$x, $y]"
        }
    }
}