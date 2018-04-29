package com.quickbite.economy.input

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g3d.particles.ParticleSorter
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Fixture
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.DebugDrawComponent
import com.quickbite.economy.gui.EditorGUI
import com.quickbite.economy.gui.GameScreenGUIManager
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.screens.GameScreen
import com.quickbite.economy.systems.RenderSystem
import com.quickbite.economy.util.*

/**
 * Created by Paha on 1/17/2017.
 * This contains data (a handler) for the InputController. Also contains larger functions
 * that are too verbose fro the input controller to make it clearer
 */
class InputHandler(val gameScreen: GameScreen) : InputProcessor{
    internal var buttonDown = -1
    internal var down = false

    internal var insideUI = false

    internal var entityClickedOn:Entity? = null
    internal var selectedEntity: Entity? = null
    internal var collidedWith = false

    internal var linkingAnotherEntity = false
    internal var linkingEntityCallback:(Entity) -> Unit = {}

    internal val queryCallback = {fixture:Fixture ->
        val entity = fixture.userData as Entity
        if(Mappers.graphic[entity].fullyShown) {
            entityClickedOn = entity
            false //Terminate
        }
        else
            true //Keep going
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        //Get the world coords
        val worldCoords = MyGame.camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val box2DCoords = Vector2(worldCoords.x* Constants.BOX2D_SCALE, worldCoords.y*Constants.BOX2D_SCALE)

        when(button){
            //If we are left clicking, we either want to place something or deselect
            Input.Buttons.LEFT -> {
                if(selectedEntity != null && EditorGUI.editingState != EditorGUI.EDITING_STATE.None) {
                    checkEditing(screenX, screenY)
                }
                //If the currently selected type is not empty, lets do something
                else if(gameScreen.currentlySelectedType.isNotEmpty()){
                    InputController.placeEntity(worldCoords, gameScreen.currentlySelectedType)
                    collidedWith = false //Reset this flag

                //If the selected type is empty, the we need to see if we are clicking on something
                }else{
                    InputController.selectEntity(box2DCoords, this)
                }
            }

            //if we are right clicking, clear out selection and UI
            Input.Buttons.RIGHT -> {
                gameScreen.currentlySelectedType = ""
                clearLinkingEntity()
                EditorGUI.closeEditor()
            }
        }

        down = false
        buttonDown = -1
        return false
    }

    private fun checkEditing(screenX:Int, screenY:Int):Boolean{
        val worldCoords = MyGame.camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 1f))
        val def = DefinitionManager.definitionMap[Mappers.identity[selectedEntity]!!.name]!!

        val transform = Mappers.transform[selectedEntity]
        val myIndex = MyGame.grid.getIndexOfGrid(transform.position.x, transform.position.y)!!
        val mouseIndex = MyGame.grid.getIndexOfGrid(worldCoords.x, worldCoords.y)

        when(EditorGUI.editingState){
            EditorGUI.EDITING_STATE.Entrance -> {
                transform.spotMap["entrance"]?.get(0)?.set(worldCoords.x, worldCoords.y)
            }

            //This is when we are adding grid spots to be blocked
            EditorGUI.EDITING_STATE.BlockGrid -> {
                val grid = Mappers.grid[selectedEntity]

                //Get my index

                //Get the index of the world position of the mouse click
                val relativeIndex = Pair(mouseIndex.first - myIndex.first, mouseIndex.second - myIndex.second)

                //If the grid spot we clicked doesn't exist in the array already...
                if(!def.gridDef.gridSpotsToBlock.any { it[0] == relativeIndex.first && it[1] == relativeIndex.second }){

                    //Unblock all existing grid spots
                    def.gridDef.gridSpotsToBlock.forEach {
                        MyGame.grid.setUnblocked(Pair(myIndex.first + it[0], myIndex.second + it[1]))
                    }

                    val list = arrayListOf<Array<Int>>()
                    def.gridDef.gridSpotsToBlock.forEach { list += it } //Add existing to the list
                    list += arrayOf(relativeIndex.first, relativeIndex.second) //Add the new to the list

                    def.gridDef.gridSpotsToBlock = list.toTypedArray() //Assign it back to the grid spots

                    //Block again all grid spots
                    def.gridDef.gridSpotsToBlock.forEach {
                        MyGame.grid.setBlocked(Pair(myIndex.first + it[0], myIndex.second + it[1]))
                    }
                }
            }

            EditorGUI.EDITING_STATE.ClearGrid -> {
                val relativeIndex = Pair(mouseIndex.first - myIndex.first, mouseIndex.second - myIndex.second)

                //Unblock all existing grid spots
                def.gridDef.gridSpotsToBlock.forEach {
                    MyGame.grid.setUnblocked(Pair(myIndex.first + it[0], myIndex.second + it[1]))
                }

                //First remove the entity from the grid
                MyGame.grid.setUnblocked(transform.position.x, transform.position.y,
                        transform.dimensions.x * 0.5f, transform.dimensions.y * 0.5f)

                val newList = def.gridDef.gridSpotsToBlock.filter { !(it[0] == relativeIndex.first && it[1] == relativeIndex.second) }
                def.gridDef.gridSpotsToBlock = newList.toTypedArray() //Reassign it

                //Unblock all existing grid spots
                def.gridDef.gridSpotsToBlock.forEach {
                    MyGame.grid.setBlocked(Pair(myIndex.first + it[0], myIndex.second + it[1]))
                }

            }
            EditorGUI.EDITING_STATE.None -> return false
        }

        return true
    }

    private fun clearLinkingEntity(){
        linkingEntityCallback = {} //Clear the entity callback
        linkingAnotherEntity = false
        RenderSystem.linkToArrow.active = false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val coords = Vector2(screenX.toFloat(), MyGame.camera.viewportHeight - screenY.toFloat())
        insideUI = false

        //For each GUIWindow in the stack, check if we're inside the window
        GameScreenGUIManager.guiStack.forEach {
            val mousePosition = it.window.stageToLocalCoordinates(coords)

            //TODO Need to figure out how to stop showing tooltips when we move onto the UI
            //If we're inside the window, set the flag to true and stop showing the tooltip
            if(it.window.hit(mousePosition.x, mousePosition.y, false) != null) {
                insideUI = true
//                GameScreenGUIManager.stopShowingTooltip()
                return@forEach
            }
        }
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        down = true
        buttonDown = button
        return false
    }

    override fun scrolled(amount: Int): Boolean {
        MyGame.camera.zoom += amount*0.1f
        MyGame.box2dCamera.zoom += amount*0.1f
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        when(keycode){
            Input.Keys.F5 -> DefinitionManager.clearAllDataAndReload()
            Input.Keys.F1 -> if(selectedEntity != null) EditorGUI.openEditor()
            Input.Keys.F2 -> DefinitionManager.resaveDefinitions()
            Input.Keys.C -> DebugDrawComponent.GLOBAL_DEBUG_CENTER = !DebugDrawComponent.GLOBAL_DEBUG_CENTER
            Input.Keys.P -> DebugDrawComponent.GLOBAL_DEBUG_PATH = !DebugDrawComponent.GLOBAL_DEBUG_PATH
            Input.Keys.E -> DebugDrawComponent.GLOBAL_DEBUG_ENTRANCE = !DebugDrawComponent.GLOBAL_DEBUG_ENTRANCE
            Input.Keys.L -> DebugDrawComponent.GLOBAL_DEBUG_SHOPLINK = !DebugDrawComponent.GLOBAL_DEBUG_SHOPLINK
            Input.Keys.B -> DebugDrawComponent.GLOBAL_DEBUG_BODY = !DebugDrawComponent.GLOBAL_DEBUG_BODY
            Input.Keys.F -> Spawner.spawnBuyer()
            Input.Keys.T -> GameScreenGUIManager.openTownWindow()
            Input.Keys.ESCAPE -> {gameScreen.currentlySelectedType = ""; GameScreenGUIManager.closeAllWindows()}
            Input.Keys.SPACE -> {
                TimeUtil.paused = !TimeUtil.paused
//                Pathfinder.delayTime = if(TimeUtil.paused) 10000000L else 5L
            }
            Input.Keys.G -> GameScreen.showGrid = !GameScreen.showGrid

            Input.Keys.PLUS ->{
                TimeUtil.timeScaleSpeedIndex = MathUtils.clamp(TimeUtil.timeScaleSpeedIndex+1, 0, TimeUtil.timeScaleSpeeds.size-1)
                TimeUtil.deltaTimeScale = TimeUtil.timeScaleSpeeds[TimeUtil.timeScaleSpeedIndex]
            }
            Input.Keys.MINUS -> {
                TimeUtil.timeScaleSpeedIndex = MathUtils.clamp(TimeUtil.timeScaleSpeedIndex-1, 0, TimeUtil.timeScaleSpeeds.size-1)
                TimeUtil.deltaTimeScale = TimeUtil.timeScaleSpeeds[TimeUtil.timeScaleSpeedIndex]
            }
        }

        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val worldCoords = MyGame.camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        if(gameScreen.currentlySelectedType.isNotEmpty() && buttonDown == Input.Buttons.LEFT) {

            //TODO Need to figure out how to do this better so we don't spam more units than we want. Use physics query?
//            val pos = Vector2(Util.roundDown(worldCoords.x + MyGame.grid.squareSize * 0.5f, MyGame.grid.squareSize).toFloat(),
//                    Util.roundDown(worldCoords.y + MyGame.grid.squareSize * 0.5f, MyGame.grid.squareSize).toFloat())
//
//            if (!MyGame.grid.getNodeAtPosition(pos.x, pos.y)!!.blocked) {
//                Factory.createObjectFromJson(gameScreen.currentlySelectedType, Vector2(pos))!!
//            }
        }

        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        return false
    }
}