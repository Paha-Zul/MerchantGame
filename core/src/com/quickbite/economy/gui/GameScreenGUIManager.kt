package com.quickbite.economy.gui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.quickbite.economy.MyGame
import com.quickbite.economy.interfaces.GuiWindow
import com.quickbite.economy.screens.GameScreen
import com.quickbite.spaceslingshot.util.EventSystem
import java.util.*

/**
 * Created by Paha on 1/30/2017.
 */
class GameScreenGUIManager(val gameScreen: GameScreen) {
    val guiStack:Stack<GuiWindow> = Stack()

    val defaultLabelStyle = Label.LabelStyle(MyGame.manager["defaultFont", BitmapFont::class.java], Color.BLACK)
    val mainTable = Table()

    val topTable = Table()

    init{
        val moneyLabel = Label("Gold: ${gameScreen.gameScreeData.playerMoney}", defaultLabelStyle)
        topTable.add(moneyLabel)

        EventSystem.onEvent("addPlayerMoney", {moneyLabel.setText("Gold: ${gameScreen.gameScreeData.playerMoney}")})
    }

    fun openEntityWindow(entity:Entity){
        guiStack.add(EntityWindow(this, entity))
    }

    fun closeWindow(window:GuiWindow){
        guiStack.remove(window)
    }

    fun update(delta:Float){
        guiStack.forEach { it.update(delta) }
    }
}