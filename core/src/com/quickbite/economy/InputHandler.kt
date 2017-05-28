package com.quickbite.economy

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Fixture
import com.quickbite.economy.components.DebugDrawComponent
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.screens.GameScreen
import com.quickbite.economy.util.Constants
import com.quickbite.economy.util.Factory
import com.quickbite.economy.util.TimeUtil
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/17/2017.
 */
class InputHandler(val gameScreen: GameScreen) : InputProcessor{
    var buttonDown = -1
    var down = false

    var entityClickedOn:Entity? = null
    var selectedEntity: Entity? = null
    var collidedWith = false

    var linkingAnotherEntity = false
    var linkingEntityCallback:(Entity) -> Unit = {}

    private val queryCallback = {fixture:Fixture ->
        val entity = fixture.userData as Entity
        entityClickedOn = entity
        false //Terminate
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        //Get the world coords
        val worldCoords = MyGame.camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val box2DCoords = Vector2(worldCoords.x* Constants.BOX2D_SCALE, worldCoords.y*Constants.BOX2D_SCALE)

        when(button){
            //If we are left clicking, we either want to place something or deselect
            Input.Buttons.LEFT -> {
                //If the currently selected type is not empty, lets do something
                if(gameScreen.currentlySelectedType.isNotEmpty()){
                    //Get the position of the closest square to the mouse. Snap to the grid!
                    val pos = Vector2(Util.roundDown(worldCoords.x + MyGame.grid.squareSize*0.5f, MyGame.grid.squareSize).toFloat(),
                            Util.roundDown(worldCoords.y + MyGame.grid.squareSize*0.5f, MyGame.grid.squareSize).toFloat())


                    val entityDef = DefinitionManager.definitionMap[gameScreen.currentlySelectedType.toLowerCase()]!!
                    val dimensions = entityDef.transformDef.physicalDimensions

                    if(!MyGame.grid.isBlocked(pos.x, pos.y, dimensions.x/2, dimensions.y/2)){
                        Factory.createObjectFromJson(gameScreen.currentlySelectedType, Vector2(pos))!!
                    }

                    collidedWith = false

                    //If the selected type is empty, the we need to see if we are clicking on something
                }else{
                    //Only clear the selected Entity if we are not linking another Entity
                    if(!linkingAnotherEntity)
                        selectedEntity = null //Clear first

                    //Query. Make sure to use the box2DCoords for this!
                    MyGame.world.QueryAABB(queryCallback, box2DCoords.x, box2DCoords.y, box2DCoords.x, box2DCoords.y)

                    //Handle the outcome
                    if(entityClickedOn == null) { //If null, close the entity table (if it happens to be open)
//                        gameScreen.gameScreenGUI.closeEntityTable()

                    //If not null, open the table for the Entity
                    }else{
                        //Call this callback (probably empty most of the time
                        linkingEntityCallback(entityClickedOn!!)

                        //Save the selected entity
                        selectedEntity = entityClickedOn

                        //If we aren't linking another entity, open another entity window
                        if(!linkingAnotherEntity)
                            gameScreen.gameScreenGUI.openEntityWindow(selectedEntity!!)

                    }

                    entityClickedOn = null //Reset this immediately
                    linkingAnotherEntity = false
                }
            }

            //if we are right clicking, clear out selection and UI
            Input.Buttons.RIGHT -> {
                gameScreen.currentlySelectedType = ""
                linkingEntityCallback = {} //Clear the entity callback
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
            Input.Keys.F5 -> DefinitionManager.clearAllDataAndReload()
            Input.Keys.C -> DebugDrawComponent.GLOBAL_DEBUG_CENTER = !DebugDrawComponent.GLOBAL_DEBUG_CENTER
            Input.Keys.P -> DebugDrawComponent.GLOBAL_DEBUG_PATH = !DebugDrawComponent.GLOBAL_DEBUG_PATH
            Input.Keys.E -> DebugDrawComponent.GLOBAL_DEBUG_ENTRANCE = !DebugDrawComponent.GLOBAL_DEBUG_ENTRANCE
            Input.Keys.L -> DebugDrawComponent.GLOBAL_DEBUG_SHOPLINK = !DebugDrawComponent.GLOBAL_DEBUG_SHOPLINK
            Input.Keys.B -> DebugDrawComponent.GLOBAL_DEBUG_BODY = !DebugDrawComponent.GLOBAL_DEBUG_BODY
            Input.Keys.T -> gameScreen.gameScreenGUI.openTownWindow()
            Input.Keys.D -> Factory.destroyAllEntities()
            Input.Keys.ESCAPE -> {gameScreen.currentlySelectedType = ""; gameScreen.gameScreenGUI.closeAllWindows()}
            Input.Keys.SPACE -> TimeUtil.pausedBonus = if(TimeUtil.pausedBonus > 0) 0 else 1
            Input.Keys.G -> gameScreen.showGrid = !gameScreen.showGrid

            Input.Keys.PLUS -> TimeUtil.deltaTimeScale = MathUtils.clamp(TimeUtil.deltaTimeScale + 1, 0, 4)
            Input.Keys.MINUS -> TimeUtil.deltaTimeScale = MathUtils.clamp(TimeUtil.deltaTimeScale - 1, 0, 4)
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