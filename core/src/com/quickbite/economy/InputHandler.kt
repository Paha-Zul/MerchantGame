package com.quickbite.economy

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.quickbite.economy.components.DebugDrawComponent
import com.quickbite.economy.screens.GameScreen
import com.quickbite.economy.util.Factory
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/17/2017.
 */
class InputHandler(val gameScreen: GameScreen) : InputProcessor{

    var down = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val worldCoords = MyGame.camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        when(button){
            Input.Buttons.LEFT -> {
                if(gameScreen.currentlySelectedType.isNotEmpty()){
                    val pos = Vector2(Util.roundDown(worldCoords.x + MyGame.grid.squareSize*0.5f, MyGame.grid.squareSize).toFloat(),
                            Util.roundDown(worldCoords.y + MyGame.grid.squareSize*0.5f, MyGame.grid.squareSize).toFloat())

                    if(!MyGame.grid.getNodeAtPosition(pos.x, pos.y)!!.blocked){
                        Factory.createObject(gameScreen.currentlySelectedType, Vector2(pos), Vector2(80f, 80f), "Workshop")!!
                    }
                }
            }
            Input.Buttons.RIGHT -> gameScreen.currentlySelectedType = ""
        }

        down = false
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
        return false
    }

    override fun scrolled(amount: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        when(keycode){
            Input.Keys.NUM_1 -> gameScreen.currentlySelectedType = "workshop"
            Input.Keys.NUM_2 -> gameScreen.currentlySelectedType = "shop"
            Input.Keys.NUM_3 -> gameScreen.currentlySelectedType = "wall"
            Input.Keys.NUM_4 -> gameScreen.currentlySelectedType = "worker"
            Input.Keys.C -> DebugDrawComponent.GLOBAL_DEBUG_CENTER = !DebugDrawComponent.GLOBAL_DEBUG_CENTER
            Input.Keys.P -> DebugDrawComponent.GLOBAL_DEBUG_PATH = !DebugDrawComponent.GLOBAL_DEBUG_PATH
            Input.Keys.E -> DebugDrawComponent.GLOBAL_DEBUG_ENTRANCE = !DebugDrawComponent.GLOBAL_DEBUG_ENTRANCE
            Input.Keys.D -> MyGame.entityEngine.removeAllEntities()
            Input.Keys.ESCAPE -> gameScreen.currentlySelectedType = ""
            Input.Keys.G -> gameScreen.showGrid = !gameScreen.showGrid
        }

        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val worldCoords = MyGame.camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        if(gameScreen.currentlySelectedType.isNotEmpty() && pointer == Input.Buttons.LEFT) {

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