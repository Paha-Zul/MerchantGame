package com.quickbite.economy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.quickbite.economy.gui.widgets.Graph
import com.quickbite.economy.interfaces.GuiWindow
import com.quickbite.economy.managers.TownManager
import com.quickbite.economy.objects.Town
import com.quickbite.economy.util.Util
import com.quickbite.spaceslingshot.util.EventSystem

/**
 * Created by Paha on 4/9/2017.
 */
class TownWindow(guiManager: GameScreenGUIManager) : GuiWindow(guiManager) {

    init{
        val style = Graph.GraphStyle(TextureRegionDrawable(TextureRegion(Util.createPixel(Color.BLACK))), Color.BLACK)
        style.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.DARK_GRAY)))
        style.lineThickness = 2f

        val graph = Graph(TownManager.getTown("Town").populationHistory.toList(), 100, style)

        contentTable.add(graph).size(400f, 300f)

//        contentTable.debugAll()

        EventSystem.onEvent("pop_change", {args ->
            val town = args[0] as Town
            graph.points = town.populationHistory.toList()
        })

        initTabs()
    }

    private fun initTabs(){
        val exitLabel = TextButton("X", defaultButtonStyle)
        exitLabel.label.setFontScale(0.17f)

        exitLabel.addListener(object: ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                close()
                return true
            }
        })

        tabTable.add().expandX().fillX()
        tabTable.add(exitLabel).right().size(32f, 32f)
    }

    override fun update(delta: Float) {
        super.update(delta)
    }

    override fun close() {
        super.close()
    }
}