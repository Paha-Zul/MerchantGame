package com.quickbite.economy

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Fixture
import com.quickbite.economy.gui.GameScreenGUIManager
import com.quickbite.economy.util.Constants
import com.quickbite.economy.util.GUIUtil

/**
 * Created by Paha on 6/12/2017.
 */
object CheckHoverOverEntity {
    var currentEntity: Entity? = null
    var newlySelectedEntity:Entity? = null
    var counter = 0f
    var showingHover = false

    val callback = { fixture:Fixture ->
        newlySelectedEntity = fixture.userData as Entity
        false
    }

    fun update(delta:Float){
        newlySelectedEntity = null
        val worldCoord = MyGame.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
        worldCoord.set(worldCoord.x*Constants.BOX2D_SCALE, worldCoord.y*Constants.BOX2D_SCALE, 0f)
        MyGame.world.QueryAABB(callback, worldCoord.x, worldCoord.y, worldCoord.x, worldCoord.y)

        //If they are not the same, assign them
        if(newlySelectedEntity != currentEntity){
            currentEntity = newlySelectedEntity
            counter = 0f
            showingHover = false
            GameScreenGUIManager.stopShowingTooltip() //Stop showing the tooltip when we change entities

        //Otherwise, as long as the current entity is not null, do stuff!
        }else if(currentEntity != null && !showingHover){
            counter += delta
            //TODO WHOA MAGIC NUMBER, FIX THIS!!!
            if(counter >= 0.4f){
                showingHover = true
                GUIUtil.makeEntityTooltip(currentEntity!!)
                GameScreenGUIManager.startShowingTooltip(GameScreenGUIManager.TooltipLocation.Mouse)
            }
        }
    }
}