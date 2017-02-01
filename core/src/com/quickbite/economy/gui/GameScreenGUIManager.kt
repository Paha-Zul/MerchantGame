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
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.*
import com.quickbite.economy.screens.GameScreen
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/30/2017.
 */
class GameScreenGUIManager(val gameScreen: GameScreen) {
    private val updateList:Array<UpdateLabel> = Array(5)

    val defaultLabelStyle = Label.LabelStyle(MyGame.manager["defaultFont", BitmapFont::class.java], Color.BLACK)
    val mainTable = Table()

    init {

    }

    fun update(delta:Float){
        updateList.forEach { it.update() }
    }

    fun openEntityTable(entity: Entity){
        mainTable.clear()

        mainTable.setSize(400f, 400f)
        mainTable.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE, 400, 600)))
        mainTable.setPosition(100f, 100f)

        val labelStyle = Label.LabelStyle(MyGame.manager["defaultFont", BitmapFont::class.java], Color.BLACK)

        val tabTable = Table()
        val bottomTable = Table()

        val sc = Mappers.selling.get(entity)
        val wc = Mappers.workforce.get(entity)
        val bc = Mappers.building.get(entity)
        val behComp = Mappers.behaviour.get(entity)
        val ic = Mappers.inventory.get(entity)

        val buildingLabel = Label("Building", labelStyle)
        buildingLabel.setFontScale(0.2f)

        val sellLabel = Label("Sell", labelStyle)
        sellLabel.setFontScale(0.2f)

        val workLabel = Label("Work", labelStyle)
        workLabel.setFontScale(0.2f)

        val behaviourLabel = Label("Beh", labelStyle)
        behaviourLabel.setFontScale(0.2f)

        val inventoryLabel = Label("Inv", labelStyle)
        inventoryLabel.setFontScale(0.2f)

        if(bc != null)
            tabTable.add(buildingLabel).spaceRight(10f)
        if(sc != null)
            tabTable.add(sellLabel).spaceRight(10f)
        if(wc != null)
            tabTable.add(workLabel).spaceRight(10f)
        if(behComp != null)
            tabTable.add(behaviourLabel).spaceRight(10f)
        if(ic != null)
            tabTable.add(inventoryLabel)

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

        inventoryLabel.addListener(object:ClickListener(){

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(bottomTable, ic)
                return true
            }
        })

        mainTable.add(tabTable).expandX().fillX()
        mainTable.row()
        mainTable.add(bottomTable).expand().fill()

        mainTable.debugAll()

        MyGame.stage.addActor(mainTable)
    }

    fun closeEntityTable(){
        mainTable.remove()
    }

    private fun loadTable(table:Table, component:Component){
        table.clear()
        updateList.clear()

        when(component.javaClass){
            BuildingComponent::class.java -> {
                val comp = component as BuildingComponent

                val typeLabel = Label("Type: ${comp.buildingType}", defaultLabelStyle)
                typeLabel.setFontScale(0.2f)
                val queueLabel = Label("Queue size: ${comp.unitQueue.size}", defaultLabelStyle)
                queueLabel.setFontScale(0.2f)

                table.add(typeLabel).expandX().fillX()
                table.row()
                table.add(queueLabel).expandX().fillX()

                updateList.add(UpdateLabel(typeLabel, { label -> label.setText("Type: ${comp.buildingType}")}))
                updateList.add(UpdateLabel(queueLabel, { label -> label.setText("Queue size: ${comp.unitQueue.size}")}))
            }
            SellingItemsComponent::class.java -> {
                val comp = component as SellingItemsComponent

                val sellLabel = Label("Selling: ${comp.sellingItems}", defaultLabelStyle)
                sellLabel.setFontScale(0.2f)

                table.add(sellLabel).expandX().fillX()

                updateList.add(UpdateLabel(sellLabel, { label -> label.setText("Selling: ${comp.sellingItems}")}))
            }
            WorkForceComponent::class.java -> {
                val comp = component as WorkForceComponent

                val spotsLabel = Label("Spots: ${comp.numWorkerSpots}", defaultLabelStyle)
                spotsLabel.setFontScale(0.2f)
                val available = Label("Available: ${comp.workersAvailable.size}", defaultLabelStyle)
                available.setFontScale(0.2f)
                val tasksLabel = Label("Tasks: ${comp.workerTasks}", defaultLabelStyle)
                tasksLabel.setFontScale(0.2f)
                tasksLabel.setWrap(true)

                table.add(spotsLabel).expandX().fillX()
                table.row()
                table.add(available).expandX().fillX()
                table.row()
                table.add(tasksLabel).expandX().fillX()

                updateList.add(UpdateLabel(spotsLabel, { label -> label.setText("Spots: ${comp.numWorkerSpots}")}))
                updateList.add(UpdateLabel(available, { label -> label.setText("Available: ${comp.workersAvailable.size}")}))
                updateList.add(UpdateLabel(tasksLabel, { label -> label.setText("Tasks: ${comp.workerTasks}")}))
            }
            BehaviourComponent::class.java -> {
                val comp = component as BehaviourComponent

                val taskLabel = Label("CurrTask: ${comp.currTask}", defaultLabelStyle)
                taskLabel.setFontScale(0.2f)
                val nameLabel = Label("CurrTaskName: ${comp.currTaskName}", defaultLabelStyle)
                nameLabel.setFontScale(0.2f)

                table.add(taskLabel).expandX().fillX()
                table.row()
                table.add(nameLabel).expandX().fillX()

                updateList.add(UpdateLabel(taskLabel, { label -> label.setText("CurrTask: ${comp.currTask}")}))
                updateList.add(UpdateLabel(nameLabel, { label -> label.setText("CurrTaskName: ${comp.currTaskName}")}))
            }
            InventoryComponent::class.java -> {
                val comp = component as InventoryComponent

                val amountLabel = Label("Amount of items: ${comp.itemMap.size}", defaultLabelStyle)
                amountLabel.setFontScale(0.2f)
                val listLabel = Label("Item List: ${comp.itemMap.values}", defaultLabelStyle)
                listLabel.setFontScale(0.2f)
                listLabel.setWrap(true)

                table.add(amountLabel).expandX().fillX()
                table.row()
                table.add(listLabel).expandX().fillX()

                updateList.add(UpdateLabel(amountLabel, { label -> label.setText("Amount of items: ${comp.itemMap.size}")}))
                updateList.add(UpdateLabel(listLabel, { label -> label.setText("Item List: ${comp.itemMap.values}")}))
            }
        }
    }

    private class UpdateLabel(val label:Label, val updateFunc:(Label)->Unit){
        fun update(){
            updateFunc.invoke(label)
        }

    }
}