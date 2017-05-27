package com.quickbite.economy.tutorialtest

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.quickbite.economy.MyGame
import com.quickbite.economy.gui.GameScreenGUIManager

/**
 * Created by Paha on 5/26/2017.
 */
object TutorialTest{

    lateinit var gameScreenGUIManager: GameScreenGUIManager
    val outlineBox:NinePatchDrawable = NinePatchDrawable(NinePatch(TextureRegion(MyGame.manager["glowingOutlineBox", Texture::class.java]), 12, 12, 12, 12))
    var bounds:Rectangle = Rectangle()
    lateinit var currActor: Actor

    var elapsed = 0f
    val lifeTime = 1f

    var grow = true

    val linearAlpha:Interpolation = Interpolation.linear

    const val OUTLINE_SCALE = 0.04f

    fun test(){
        currActor = gameScreenGUIManager.openTownWindow()!!.window
    }

    fun render(delta:Float, batch:SpriteBatch){
        getBounds(calcAlpha(delta))

        outlineBox.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height)
    }

    fun calcAlpha(delta:Float):Float{
        when(grow) {
            true -> elapsed += delta*2f
            else -> elapsed -= delta*2f
        }

        val progress = Math.min(1f, elapsed/ lifeTime)
        val alpha = linearAlpha.apply(progress)

        when{
            elapsed > 1 -> {elapsed = 1f; grow = false}
            elapsed < 0 -> {elapsed = 0f; grow = true}
        }

        return alpha
    }

    fun getBounds(alpha:Float){
        var position = currActor.localToStageCoordinates(Vector2(0f,0f))
        position = currActor.stage.stageToScreenCoordinates(position)

        val windowWidth = currActor.width
        val windowHeight = currActor.height

        val startX = position.x - windowWidth*alpha*0.02f
        val startY = MyGame.UICamera.viewportHeight - position.y - windowHeight*alpha*0.02f
        val width = windowWidth + windowWidth*alpha*0.04f
        val height = windowHeight + windowHeight*alpha*0.04f

        //For the Y position we have to subtract from the height of the viewport. Libgdx draws from bottom left but we get
        //coords from the top left...
        bounds.set(startX, startY, width, height)
    }
}