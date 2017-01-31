package com.quickbite.economy.gui

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.BehaviourComponent
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.components.SellingItemsComponent
import com.quickbite.economy.components.WorkForceComponent
import com.quickbite.economy.screens.GameScreen
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/30/2017.
 */
class GameScreenGUIManager(val gameScreen: GameScreen) {
    val defaultLabelStyle = Label.LabelStyle(MyGame.manager["defaultFont", BitmapFont::class.java], Color.BLACK)
    val mainTable = Table()

    init {

    }

    fun openEntityTable(entity: Entity){
        mainTable.clear()

        mainTable.setSize(400f, 600f)
        mainTable.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE, 400, 600)))
        mainTable.setPosition(100f, 100f)

        val labelStyle = Label.LabelStyle(MyGame.manager["defaultFont", BitmapFont::class.java], Color.BLACK)

        val tabTable = Table()
        val bottomTable = Table()

        val sc = Mappers.selling.get(entity)
        val wc = Mappers.workforce.get(entity)
        val bc = Mappers.building.get(entity)
        val behComp = Mappers.behaviour.get(entity)

        val buildingLabel = Label("Building", labelStyle)
        buildingLabel.setFontScale(0.2f)

        val sellLabel = Label("Sell", labelStyle)
        sellLabel.setFontScale(0.2f)

        val workLabel = Label("Work", labelStyle)
        workLabel.setFontScale(0.2f)

        val behaviourLabel = Label("Beh", labelStyle)
        behaviourLabel.setFontScale(0.2f)

        if(bc != null)
            tabTable.add(buildingLabel).spaceRight(10f)
        if(sc != null)
            tabTable.add(sellLabel).spaceRight(10f)
        if(wc != null)
            tabTable.add(workLabel).spaceRight(10f)
        if(behComp != null)
            tabTable.add(behaviourLabel)

        buildingLabel.addListener(object:ClickListener(){

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(bottomTable, bc)
                return true
            }
        })

        sellLabel.addListener(object:ClickListener(){

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(bottomTable, sc)
                return true
            }
        })

        workLabel.addListener(object:ClickListener(){

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(bottomTable, wc)
                return true
            }
        })

        behaviourLabel.addListener(object:ClickListener(){

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(bottomTable, behComp)
                return true
            }
        })

        mainTable.add(tabTable)
        mainTable.row()
        mainTable.add(bottomTable).expandY().fillY()

        MyGame.stage.addActor(mainTable)
    }

    fun closeEntityTable(){
        mainTable.remove()
    }

    private fun loadTable(table:Table, component:Component){
        table.clear()
        when(component.javaClass){
            BuildingComponent::class.java -> {
                val comp = component as BuildingComponent

                val typeLabel = Label("Type: ${comp.buildingType}", defaultLabelStyle)
                typeLabel.setFontScale(0.2f)
                val queueLabel = Label("Queue size: ${comp.unitQueue.size}", defaultLabelStyle)
                queueLabel.setFontScale(0.2f)

                table.add(typeLabel)
                table.row()
                table.add(queueLabel)
            }
            SellingItemsComponent::class.java -> {
                val comp = component as SellingItemsComponent

                val sellLabel = Label("Selling: ${comp.sellingItems}", defaultLabelStyle)
                sellLabel.setFontScale(0.2f)

                table.add(sellLabel)
            }
            WorkForceComponent::class.java -> {
                val comp = component as WorkForceComponent

                val spotsLabel = Label("Spots: ${comp.numWorkerSpots}", defaultLabelStyle)
                spotsLabel.setFontScale(0.2f)
                val available = Label("Available: ${comp.workersAvailable.size}", defaultLabelStyle)
                available.setFontScale(0.2f)
                val tasksLabel = Label("Tasks: ${comp.workerTasks}", defaultLabelStyle)
                tasksLabel.setFontScale(0.2f)

                table.add(spotsLabel)
                table.row()
                table.add(available)
                table.row()
                table.add(tasksLabel)
            }
            BehaviourComponent::class.java -> {
                val comp = component as BehaviourComponent

                val taskLabel = Label("CurrTask: ${comp.currTask}", defaultLabelStyle)
                taskLabel.setFontScale(0.2f)
                val nameLabel = Label("CurrTaskName: ${comp.currTaskName}", defaultLabelStyle)
                nameLabel.setFontScale(0.2f)

                table.add(taskLabel)
                table.row()
                table.add(nameLabel)
            }
        }
    }
}