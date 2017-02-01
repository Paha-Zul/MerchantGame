package com.quickbite.economy

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Fixture
import com.quickbite.economy.components.DebugDrawComponent
import com.quickbite.economy.screens.GameScreen
import com.quickbite.economy.util.Factory
import com.quickbite.economy.util.TimeUtil
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/17/2017.
 */
class InputHandler(val gameScreen: GameScreen) : InputProcessor{
    var buttonDown = -1
    var down = false
    var selectedEntity: Entity? = null

    private val queryCallback = {fixture:Fixture ->
        val entity = fixture.userData as Entity
        selectedEntity = entity
        gameScreen.gameScreenGUI.openEntityTable(selectedEntity!!)
        false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        //Get the world coords
        val worldCoords = MyGame.camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        when(button){
            //If we are left clicking, we either want to place something or deselect
            Input.Buttons.LEFT -> {
                //If the currently selected type is not empty, lets do something
                if(gameScreen.currentlySelectedType.isNotEmpty()){
                    //Get the position of the closest square to the mouse. Snap to the grid!
                    val pos = Vector2(Util.roundDown(worldCoords.x + MyGame.grid.squareSize*0.5f, MyGame.grid.squareSize).toFloat(),
                            Util.roundDown(worldCoords.y + MyGame.grid.squareSize*0.5f, MyGame.grid.squareSize).toFloat())

                    //If the grid node isn't already blocked, create the object there
                    if(!MyGame.grid.getNodeAtPosition(pos.x, pos.y)!!.blocked){
                        Factory.createObject(gameScreen.currentlySelectedType, Vector2(pos), Vector2(80f, 80f), "Workshop")!!
                    }

                //If the selected type is empty, the we need to see if we are clicking on something
                }else{
                    selectedEntity = null //Clear first
                    //Query
                    MyGame.world.QueryAABB(queryCallback, worldCoords.x, worldCoords.y, worldCoords.x, worldCoords.y)
                    //Handle the outcome
                    if(selectedEntity == null)
                        gameScreen.gameScreenGUI.closeEntityTable()
                }
            }
            //if we are right clicking, clear out selection and UI
            Input.Buttons.RIGHT -> {
                gameScreen.currentlySelectedType = ""
                gameScreen.gameScreenGUI.closeEntityTable()
            }
        }

        down = false
        buttonDown = -1
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
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
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        when(keycode){
            Input.Keys.NUM_1 -> gameScreen.currentlySelectedType = "workshop"
            Input.Keys.NUM_2 -> gameScreen.currentlySelectedType = "shop"
            Input.Keys.NUM_3 -> gameScreen.currentlySelectedType = "stockpile"
            Input.Keys.NUM_4 -> gameScreen.currentlySelectedType = "wall"
            Input.Keys.NUM_5 -> gameScreen.currentlySelectedType = "worker"
            Input.Keys.NUM_6 -> gameScreen.currentlySelectedType = "buyer"
            Input.Keys.C -> DebugDrawComponent.GLOBAL_DEBUG_CENTER = !DebugDrawComponent.GLOBAL_DEBUG_CENTER
            Input.Keys.P -> DebugDrawComponent.GLOBAL_DEBUG_PATH = !DebugDrawComponent.GLOBAL_DEBUG_PATH
            Input.Keys.E -> DebugDrawComponent.GLOBAL_DEBUG_ENTRANCE = !DebugDrawComponent.GLOBAL_DEBUG_ENTRANCE
            Input.Keys.L -> DebugDrawComponent.GLOBAL_DEBUG_SHOPLINK = !DebugDrawComponent.GLOBAL_DEBUG_SHOPLINK
            Input.Keys.B -> DebugDrawComponent.GLOBAL_DEBUG_BODY = !DebugDrawComponent.GLOBAL_DEBUG_BODY
            Input.Keys.D -> Factory.destroyAllEntities()
            Input.Keys.ESCAPE -> gameScreen.currentlySelectedType = ""
            Input.Keys.SPACE -> TimeUtil.deltaTimeScale = if(TimeUtil.deltaTimeScale > 0) 0f else 1f
            Input.Keys.G -> gameScreen.showGrid = !gameScreen.showGrid
        }

        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val worldCoords = MyGame.camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        if(gameScreen.currentlySelectedType.isNotEmpty() && buttonDown == Input.Buttons.LEFT) {

            val pos = Vector2(Util.roundDown(worldCoords.x + MyGame.grid.squareSize * 0.5f, MyGame.grid.squareSize).toFloat(),
                    Util.roundDown(worldCoords.y + MyGame.grid.squareSize * 0.5f, MyGame.grid.squareSize).toFloat())

            if (!MyGame.grid.getNodeAtPosition(pos.x, pos.y)!!.blocked) {
                Factory.createObject(gameScreen.currentlySelectedType, Vector2(pos), Vector2(80f, 80f), "Workshop")!!
            }
        }

        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        return false
    }
}