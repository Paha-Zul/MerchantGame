package com.quickbite.economy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.quickbite.economy.MyGame
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.PopulationChangeEvent
import com.quickbite.economy.gui.widgets.Graph
import com.quickbite.economy.interfaces.GUIWindow
import com.quickbite.economy.managers.TownManager
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 4/9/2017.
 */
class TownWindow(guiManager: GameScreenGUIManager) : GUIWindow(guiManager) {
    lateinit var updatePopGraphEvent:GameEventSystem.GameEventRegistration
    val town by lazy { TownManager.getTown("Town") }

    init{
        window.titleLabel.setText("Town")
        initTabs()
        changeTabs("pop")
    }

    private fun initTabs(){
        val exitButton = TextButton("x", defaultTextButtonStyle)

        exitButton.addListener(object: ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                close()
            }
        })

        tabTable.add().expandX().fillX()
        tabTable.add(exitButton).right().size(32f)
    }

    private fun changeTabs(type:String){
        changedTabsFunc()
        changedTabsFunc = {}

        updateFuncsList.clear()

        when(type){
            "pop" -> setupPopTab()
        }
    }

    private fun setupPopTab(){
        contentTable.clear()

        val style = Graph.GraphStyle(TextureRegionDrawable(TextureRegion(Util.createPixel(Color.BLACK))), Color.BLACK, MyGame.defaultFont14)
        style.graphBackground = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.DARK_GRAY)))
        style.lineThickness = 2f

        val popTitleLabel = Label("Population:", defaultLabelStyle)
        val popAmtLabel = Label("${town.population.toInt()}", defaultLabelStyle)

        //TODO These should probably become progress bars
        val needsTitleLabel = Label("Needs:", defaultLabelStyle)
        val needsAmtLabel = Label("${town.needsRating}", defaultLabelStyle)

        val luxuryTitleLabel = Label("Luxury:", defaultLabelStyle)
        val luxuryAmtLabel = Label("${town.luxuryRating}", defaultLabelStyle)

        val graph = Graph(TownManager.getTown("Town").populationHistory.queue.toList(), 100, style)

        updatePopGraphEvent = GameEventSystem.subscribe<PopulationChangeEvent> {
            graph.points = it.popHistory
        }

        updateFuncsList.add {
            popAmtLabel.setText("${town.population}")
            needsAmtLabel.setText("${town.needsRating}")
            luxuryAmtLabel.setText("${town.luxuryRating}")
        }

        changedTabsFunc = { GameEventSystem.unsubscribe(updatePopGraphEvent) }

        contentTable.add(needsTitleLabel).uniformX()
        contentTable.add(needsAmtLabel).uniformX()
        contentTable.add().growX()
        contentTable.row()
        contentTable.add(luxuryTitleLabel).uniformX()
        contentTable.add(luxuryAmtLabel).uniformX()
        contentTable.add().growX()
        contentTable.row()
        contentTable.add(popTitleLabel).uniformX()
        contentTable.add(popAmtLabel).uniformX()
        contentTable.add().growX()
        contentTable.row()
        contentTable.add(graph).size(300f, 200f).colspan(3)

        contentTable.setFillParent(true)
        contentTable.top().left()
    }

    override fun update(delta: Float) {
        super.update(delta)
    }

    override fun close() {
        GameEventSystem.unsubscribe(updatePopGraphEvent)
        super.close()
    }
}