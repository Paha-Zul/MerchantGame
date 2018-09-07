package com.quickbite.economy.gui.entity

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.components.WorkForceComponent
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ReloadGUIEvent
import com.quickbite.economy.gui.EntityWindowController
import com.quickbite.economy.gui.GUIUtil
import com.quickbite.economy.gui.GameScreenGUIManager
import com.quickbite.economy.interfaces.IEntCompWindow
import com.quickbite.economy.util.Util
import com.quickbite.economy.util.objects.SelectedWorkerAndTable

class EntWorkforceWindow : IEntCompWindow{

    private var selectedWorkers = Array<SelectedWorkerAndTable>()

    override fun open(window:EntityWindow, comp:Component, table: Table){
        //ShiftPlanningWindow(comp).openTable()
        var comp = comp as WorkForceComponent

        val scrollPaneStyle = ScrollPane.ScrollPaneStyle()
        scrollPaneStyle.background = window.darkBackgroundDrawable
        scrollPaneStyle.vScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        scrollPaneStyle.vScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color(Color.BLACK))))
        scrollPaneStyle.hScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        scrollPaneStyle.hScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color(Color.BLACK))))

        val buttonStyle = Button.ButtonStyle()
        buttonStyle.up = window.buttonBackgroundDrawable

        //Our worker list table
        val workerListTable = Table()
        workerListTable.top()

        //Our scroll pane for the worker list table to sit inside
        val workerListScrollPane = ScrollPane(workerListTable, scrollPaneStyle)
        workerListScrollPane.setScrollingDisabled(true, false)

        //The worker tasks and amounts table
        val workerTasksAndAmountsTables = Table()
//        workerTasksAndAmountsTables.background = darkBackgroundDrawable

        //Here we populate the worker tasks and amounts table
        GUIUtil.populateWorkerTasksAndAmountsTable(comp, workerTasksAndAmountsTables, window.defaultLabelStyle)
        //This populates the actual worker list
        GUIUtil.populateWorkerTable(comp, selectedWorkers, workerListTable,
                window.defaultLabelStyle, window.defaultTextButtonStyle, GameScreenGUIManager)

        val workerTaskList = Table()

        /** This sets the tasks that are available to set to each worker */
        comp.workerTasksLimits.forEach { (taskName, amount) ->
            val taskNameLabel = Label(taskName, window.defaultLabelStyle)
            taskNameLabel.setFontScale(1f)

            workerTaskList.add(taskNameLabel).space(0f, 5f, 0f, 5f)

            //When we click the task name label: if it's not owned by the worker, remove it. Otherwise, add it
            taskNameLabel.addListener(object: ClickListener(){
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    super.clicked(event, x, y)
                    val taskNameText = taskNameLabel.text.toString()

                    //Any time this task is clicked, we want to repopulate the worker tasks and amount AND the actual worker list
                    EntityWindowController.addTaskToWorkers(taskNameText, selectedWorkers, window.entity)
                    GUIUtil.populateWorkerTasksAndAmountsTable(comp, workerTasksAndAmountsTables, window.defaultLabelStyle)
                    GUIUtil.populateWorkerTable(comp, selectedWorkers, workerListTable,
                            window.defaultLabelStyle, window.defaultTextButtonStyle, GameScreenGUIManager)
                }
            })
        }

        //The button to open the hire window
        val hireButton = TextButton("Hire", window.defaultTextButtonStyle)
        hireButton.label.setFontScale(1f)

        //Hire button listener
        hireButton.addListener(object: ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                GameScreenGUIManager.openHireWindow(window.entity)
            }
        })

        //An event to listen for a worker to be hired
        val updateEvent = GameEventSystem.subscribe<ReloadGUIEvent> {
            GUIUtil.populateWorkerTable(comp, selectedWorkers, workerListTable, window.defaultLabelStyle,
                    window.defaultTextButtonStyle, GameScreenGUIManager)
            GUIUtil.populateWorkerTasksAndAmountsTable(comp, workerTasksAndAmountsTables, window.defaultLabelStyle)
        }

        //Remember to remove this from the event system
        window.changedTabsFunc = { GameEventSystem.unsubscribe(updateEvent) }

        //Add our main scroll pane and the hiring button
        table.add(workerTasksAndAmountsTables).colspan(1).spaceBottom(5f).padTop(5f)
        table.add().growX()
        table.row()
        table.add(workerListScrollPane).grow().top().colspan(2).height(225f)
        table.row()
        table.add(workerTaskList)
        table.add(hireButton)

    }
}