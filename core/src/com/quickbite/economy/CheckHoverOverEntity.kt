package com.quickbite.economy

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Fixture
import com.quickbite.economy.gui.GameScreenGUIManager
import com.quickbite.economy.screens.GameScreen
import com.quickbite.economy.util.Constants
import com.quickbite.economy.util.GUIUtil
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 6/12/2017.
 */
object CheckHoverOverEntity {
    lateinit var gameScreen:GameScreen
    var currentEntity: Entity? = null
    var newlySelectedEntity:Entity? = null
    var counter = 0f
    var showingHover = false

    val callback = { fixture:Fixture ->
        val entity = fixture.userData as Entity
        val passed:Boolean
        if(Mappers.graphic[entity].fullyShown) {
            newlySelectedEntity = fixture.userData as Entity
            passed = false
        }else
            passed = true

        passed
    }

    fun update(delta:Float){
        newlySelectedEntity = null
        val worldCoord = MyGame.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
        worldCoord.set(worldCoord.x*Constants.BOX2D_SCALE, worldCoord.y*Constants.BOX2D_SCALE, 0f)
        MyGame.world.QueryAABB(callback, worldCoord.x, worldCoord.y, worldCoord.x, worldCoord.y)

        //If they are not the same, assign them
        if(newlySelectedEntity != currentEntity){
            switchEntities()

        //Otherwise, as long as the current entity is not null, do stuff!
        }else if(currentEntity != null && !showingHover){
            showTooltip(delta)
        }
    }

    private fun switchEntities(){
        currentEntity = newlySelectedEntity
        counter = 0f
        showingHover = false
        GameScreenGUIManager.stopShowingTooltip() //Stop showing the tooltip when we change entities
    }

    private fun showTooltip(delta:Float){
        counter += delta
        //TODO WHOA MAGIC NUMBER, FIX THIS!!!
        if(counter >= 0.4f && !gameScreen.inputHandler.insideUI){
            showingHover = true
            GUIUtil.makeEntityTooltip(currentEntity!!)
            GameScreenGUIManager.startShowingTooltip(GameScreenGUIManager.TooltipLocation.Mouse)
        }
    }
}