package com.quickbite.economy.gui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.quickbite.economy.MyGame
import com.quickbite.economy.interfaces.GuiWindow
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.screens.GameScreen
import com.quickbite.economy.util.TimeOfDay
import com.quickbite.spaceslingshot.util.EventSystem
import java.util.*

/**
 * Created by Paha on 1/30/2017.
 */
class GameScreenGUIManager(val gameScreen: GameScreen) {
    val guiStack:Stack<GuiWindow> = Stack()

    val defaultLabelStyle = Label.LabelStyle(MyGame.manager["defaultFont", BitmapFont::class.java], Color.BLACK)
    val bottomTable = Table()

    val topTable = Table()

    var timeOfDayLabel:Label

    class ImageEntityLink(val imageName:String, val entityName:String)

    init{
        val moneyLabel = Label("Gold: ${gameScreen.gameScreeData.playerMoney}", defaultLabelStyle)
        moneyLabel.setFontScale(0.2f)
        moneyLabel.setAlignment(Align.center)

        timeOfDayLabel = Label(TimeOfDay.toString(), defaultLabelStyle)
        timeOfDayLabel.setFontScale(0.2f)
        timeOfDayLabel.setAlignment(Align.center)

        topTable.add(moneyLabel).width(200f)
        topTable.row()
        topTable.add(timeOfDayLabel).width(200f)
        topTable.setPosition(MyGame.camera.viewportWidth/2f, MyGame.camera.viewportHeight - 25f)

        EventSystem.onEvent("addPlayerMoney", {moneyLabel.setText("Gold: ${gameScreen.gameScreeData.playerMoney}")})

        setupBottomTable()

        MyGame.stage.addActor(topTable)
        MyGame.stage.addActor(bottomTable)
    }

    private fun setupBottomTable(){
        val list = DefinitionManager.definitionMap.values.toList()
        list.forEach { def ->
            val buttonStyle = ImageButton.ImageButtonStyle()
            buttonStyle.imageUp = TextureRegionDrawable(TextureRegion(MyGame.manager[def.graphicDef.graphicName, Texture::class.java]))

            val button = ImageButton(buttonStyle)

            button.addListener(object: ClickListener(){
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    gameScreen.currentlySelectedType = def.name
                    return true
                }
            })

            bottomTable.add(button).size(64f)
        }

        bottomTable.width = MyGame.camera.viewportWidth
        bottomTable.height = 100f

        bottomTable.addListener(object:ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })
    }

    fun openEntityWindow(entity:Entity){
        guiStack.add(EntityWindow(this, entity))
    }

    fun closeWindow(window:GuiWindow){
        guiStack.remove(window)
    }

    fun update(delta:Float){
        guiStack.forEach { it.update(delta) }

        timeOfDayLabel.setText("D: ${TimeOfDay.day}, T: $TimeOfDay")
    }
}