package com.quickbite.economy.gui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ReloadGUIEvent
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.util.Factory
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 4/23/2017.
 * A window that lists available workers to hire
 */
class HireWorkerWindow(val workforceEntity: Entity) : GUIWindow() {
    val list = mutableListOf<String>()

    init{
        window.setSize(150f, 400f)
        tabTable.remove() //We don't want the tab table!

        val firstNames = DefinitionManager.names.firstNames
        val lastNames = DefinitionManager.names.lastNames

        //TODO We probably want a persistent list somewhere instead of randomly generating each time we open the window
        list.add(firstNames[MathUtils.random(firstNames.size-1)])
        list.add(firstNames[MathUtils.random(firstNames.size-1)])
        list.add(firstNames[MathUtils.random(firstNames.size-1)])
        list.add(firstNames[MathUtils.random(firstNames.size-1)])
        list.add(firstNames[MathUtils.random(firstNames.size-1)])

        val workforce = Mappers.workforce[workforceEntity]

        list.forEach { name ->
            val workerButton = TextButton(name, defaultTextButtonStyle)

            contentTable.add(workerButton).growX().height(40f).spaceTop(10f)
            contentTable.row()

            workerButton.addListener(object: ChangeListener(){
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    //Check to make sure we can add a worker
                    if(workforce.workersAvailable.size < workforce.numWorkerSpots) {
                        val entity = Factory.createObjectFromJson("worker", Vector2(-1000f, 0f))!! //Create the worker
                        Util.assignWorkerToBuilding(entity, workforceEntity) //Assign it to the building
                        GameEventSystem.fire(ReloadGUIEvent()) //Fire an event to reload the GUI
                        list.remove(name) //Remove the possible worker from the list, we just hired him!
                        workerButton.remove() //Remove the button
                        //TODO Removing this button leaves weird spacing... maybe rebuild the list or something?
                    }
                }
            })
        }

        contentTable.background = this.darkBackgroundDrawable
    }
}