package com.quickbite.economy.gui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.quickbite.economy.addChangeListener
import com.quickbite.economy.interfaces.GUIWindow
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.util.Factory
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 4/23/2017.
 * A window that lists available workers to hire
 */
class HireWorkerWindow(guiManager: GameScreenGUIManager, val workforceEntity: Entity) : GUIWindow(guiManager) {
    val list = mutableListOf<String>()

    init{
        window.setSize(200f, 400f)

        val exitButton = TextButton("X", defaultTextButtonStyle)
        exitButton.label.setFontScale(0.15f)

        tabTable.add().expandX().fillX()
        tabTable.add(exitButton).right().width(32f)

        val firstNames = DefinitionManager.names.firstNames
        val lastNames = DefinitionManager.names.lastNames

        //TODO We probably want a persistent list somewhere instead of randomly generating each time we open the window
        list.add(firstNames[MathUtils.random(firstNames.size-1)])
        list.add(firstNames[MathUtils.random(firstNames.size-1)])
        list.add(firstNames[MathUtils.random(firstNames.size-1)])
        list.add(firstNames[MathUtils.random(firstNames.size-1)])
        list.add(firstNames[MathUtils.random(firstNames.size-1)])

        list.forEach { name ->
            val workerButton = TextButton(name, defaultTextButtonStyle)
            workerButton.label.setFontScale(0.2f)

            contentTable.add(workerButton).width(150f).spaceTop(20f)
            contentTable.row()

            workerButton.addListener(object: ChangeListener(){
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    list.remove(name)
                    workerButton.remove()
                    val entity = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!!
                    Util.assignWorkerToBuilding(entity, workforceEntity)
//                    close()
                }
            })
        }

        exitButton.addChangeListener { _, _ ->  close()}

        contentTable.background = this.darkBackgroundDrawable
    }
}