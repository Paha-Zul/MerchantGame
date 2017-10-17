package com.quickbite.economy.input

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.components.SellingItemsComponent
import com.quickbite.economy.gui.GameScreenGUIManager
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.managers.TownManager
import com.quickbite.economy.util.Factory
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util
import com.quickbite.economy.util.objects.SellingItemData

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

    /**
     * Selects am Entity that is at the passed in box2DCoords
     * @param box2DCoords These are coordinates that have been translated to Box2D world coords.
     * @param inputHandler The input handler to use for variables and such
     */
    internal fun selectEntity(box2DCoords:Vector2, inputHandler: InputHandler){
        //Only clear the selected Entity if we are not linking another Entity
        if(!inputHandler.linkingAnotherEntity)
            inputHandler.selectedEntity = null //Clear first

        //Query. Make sure to use the box2DCoords for this!
        MyGame.world.QueryAABB(inputHandler.queryCallback, box2DCoords.x, box2DCoords.y, box2DCoords.x, box2DCoords.y)

        //Handle the outcome
        if(inputHandler.entityClickedOn == null) { //If null, close the entity table (if it happens to be open)
            inputHandler.linkingAnotherEntity = false //Clear the linking flag

            //If not null, open the table for the Entity
        }else{
            //If we're not inside a UI box, try to click on something!
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
    }

    /**
     * Links an Entity (its items) to another Entity that will resell the items (like from a workshop to a shop)
     * @param entityToLink The Entity that will be linked to another
     * @param entToLinkTo The Entity we are linking to
     */
    fun linkEntityForReselling(entityToLink: Entity, entToLinkTo:Entity){
        if(entityToLink != entToLinkTo){
            val otherBuilding = Mappers.building[entityToLink]
            val otherSelling:SellingItemsComponent? = Mappers.selling[entityToLink]

            //If we aren't linking to a building, then don't do this...
            if(otherBuilding != null) {
                //TODO this needs to be more sophisticated, maybe remove the selling potential of the workshop?
                if (otherBuilding.buildingType == BuildingComponent.BuildingType.Workshop) {

                    //Try to add stuff from the selling list
                    if(otherSelling != null){
                        otherSelling.currSellingItems.forEach { (itemName, itemPrice) ->
                            Util.addItemToEntityReselling(entToLinkTo, itemName, SellingItemData.ItemSource.Workshop, entityToLink)
                        }

                        otherSelling.currSellingItems.clear()
                    //If we don't have a selling component, let's add from the output list (this is useful for stuff like farms
                    //that don't sell their items)
                    }else{
                        val inv = Mappers.inventory[entityToLink]
                        inv.outputItems.forEach {
                            if(it.key != "none" && it.key != "all")
                                Util.addItemToEntityReselling(entToLinkTo, it.key, SellingItemData.ItemSource.Workshop, entityToLink)
                        }
                    }
                }
            }
        }
    }
}