package com.quickbite.economy.input

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.quickbite.economy.MyGame
import com.quickbite.economy.gui.GameScreenGUIManager
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.managers.TownManager
import com.quickbite.economy.util.Factory
import com.quickbite.economy.util.Util

object InputController {
    internal fun placeEntity(worldCoords: Vector3, currentlySelectedType:String):Boolean{
        val def = DefinitionManager.constructionDefMap[currentlySelectedType]!!
        val town = TownManager.getTown("Town")

        //TODO Add more restrictions here, like materials needed
        if(town.money < def.cost){
            return false
        }

        town.money -= def.cost

        //Get the position of the closest square to the mouse. Snap to the grid!
        val pos = Vector2(Util.roundDown(worldCoords.x + MyGame.grid.squareSize*0.5f, MyGame.grid.squareSize).toFloat(),
                Util.roundDown(worldCoords.y + MyGame.grid.squareSize*0.5f, MyGame.grid.squareSize).toFloat())


        val entityDef = DefinitionManager.definitionMap[currentlySelectedType.toLowerCase()]!!
        val dimensions = entityDef.transformDef.physicalDimensions

        if(!MyGame.grid.isBlocked(pos.x, pos.y, dimensions.x/2, dimensions.y/2)){
            Factory.createObjectFromJson(currentlySelectedType, Vector2(pos))!!
            return true
        }

        return false
    }

    internal fun selectEntity(box2DCoords:Vector2, inputHandler: InputHandler){
        //Only clear the selected Entity if we are not linking another Entity
        if(!inputHandler.linkingAnotherEntity)
            inputHandler.selectedEntity = null //Clear first

        //Query. Make sure to use the box2DCoords for this!
        MyGame.world.QueryAABB(inputHandler.queryCallback, box2DCoords.x, box2DCoords.y, box2DCoords.x, box2DCoords.y)

        //Handle the outcome
        if(inputHandler.entityClickedOn == null) { //If null, close the entity table (if it happens to be open)
//                        gameScreen.gameScreenGUI.closeEntityTable()

            //If not null, open the table for the Entity
        }else{
            if(!inputHandler.insideUI) {
                //Call this callback (probably empty most of the moveTime
                inputHandler.linkingEntityCallback(inputHandler.entityClickedOn!!)

                //Save the selected entity
                inputHandler.selectedEntity = inputHandler.entityClickedOn

                //If we aren't linking another entity, open another entity window
                if (!inputHandler.linkingAnotherEntity)
                    GameScreenGUIManager.openEntityWindow(inputHandler.selectedEntity!!)
            }
        }

        inputHandler.entityClickedOn = null //Reset this immediately
        inputHandler.linkingAnotherEntity = false
    }
}