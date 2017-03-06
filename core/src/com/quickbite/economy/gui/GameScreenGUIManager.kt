package com.quickbite.economy.gui

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.*
import com.quickbite.economy.screens.GameScreen
import com.quickbite.economy.util.EntityListLink
import com.quickbite.economy.util.ItemPriceLink
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util
import com.quickbite.spaceslingshot.util.EventSystem
import java.util.*

/**
 * Created by Paha on 1/30/2017.
 */
class GameScreenGUIManager(val gameScreen: GameScreen) {
    private val topTable:Table = Table()

    private val updateList:Array<UpdateLabel> = Array(5)
    private val updateMap:HashMap<String, ()->Unit> = hashMapOf()
    private var changedTabsFunc:()->Unit = {}

    private var currentlyDisplayingComponent:Component? = null

    private var currentlySelectedEntity:Entity? = null

    val defaultLabelStyle = Label.LabelStyle(MyGame.manager["defaultFont", BitmapFont::class.java], Color.BLACK)
    val mainTable = Table()

    init {
        val moneyLabel = Label("Gold: ${gameScreen.gameScreeData.playerMoney}", defaultLabelStyle)
        topTable.add(moneyLabel)

        EventSystem.onEvent("addPlayerMoney", {moneyLabel.setText("Gold: ${gameScreen.gameScreeData.playerMoney}")})
    }

    fun update(delta:Float){
        updateList.forEach { it.update() }
    }

    fun openEntityTable(entity: Entity){
        currentlySelectedEntity = entity

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
        val rc = Mappers.reselling.get(entity)

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

        val resellLabel = Label("Resell", labelStyle)
        resellLabel.setFontScale(0.2f)

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
        if(rc != null)
            tabTable.add(resellLabel)

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

        resellLabel.addListener(object:ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(bottomTable, rc)
                return true
            }
        })

        //When we select a new building, try to display whatever section we were displaying on the last one
        if(currentlyDisplayingComponent != null)
            loadTable(bottomTable, currentlyDisplayingComponent!!)

        mainTable.add(tabTable).expandX().fillX()
        mainTable.row()
        mainTable.add(bottomTable).expand().fill()

        mainTable.debugAll()

        MyGame.stage.addActor(mainTable)
    }

    fun closeEntityTable(){
        mainTable.remove()
        currentlySelectedEntity = null
    }

    private fun loadTable(table:Table, component:Component){
        table.clear()
        updateList.clear()

        changedTabsFunc.invoke()

        when(component.javaClass){
            BuildingComponent::class.java -> setupBuildingTable(table, component as BuildingComponent)
            SellingItemsComponent::class.java -> setupSellingTable(table, component as SellingItemsComponent)
            WorkForceComponent::class.java -> setupWorkforceTable(table, component as WorkForceComponent)
            BehaviourComponent::class.java -> setupBehaviourTable(table, component as BehaviourComponent)
            InventoryComponent::class.java -> setupInventoryTable(table, component as InventoryComponent)
            ResellingItemsComponent::class.java -> setupResellingTable(table, component as ResellingItemsComponent)
        }

        currentlyDisplayingComponent = component
    }

    private fun setupBuildingTable(table:Table, comp:BuildingComponent){
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

    private fun setupWorkforceTable(table:Table, comp:WorkForceComponent){
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

        currentlyDisplayingComponent = comp
    }

    private fun setupBehaviourTable(table:Table, comp:BehaviourComponent){
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

    private fun setupInventoryTable(table:Table, comp:InventoryComponent){
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

    private fun setupResellingTable(table:Table, comp:ResellingItemsComponent){
        val buttonStyle = TextButton.TextButtonStyle()
        buttonStyle.up = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.CYAN)))
        buttonStyle.font = MyGame.manager["defaultFont", BitmapFont::class.java]
        buttonStyle.fontColor = Color.WHITE

        val linkButton = TextButton("Link", buttonStyle)
        linkButton.label.setFontScale(0.2f)

//        val amountLabel = Label("Amount of items: ${comp.itemMap.size}", defaultLabelStyle)
//        amountLabel.setFontScale(0.2f)
//        val listLabel = Label("Item List: ${comp.itemMap.values}", defaultLabelStyle)
//        listLabel.setFontScale(0.2f)
//        listLabel.setWrap(true)

        table.add(linkButton)

        linkButton.addListener(object:ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                //TODO Probably want to clean this up
                gameScreen.inputHandler.linkingAnotherEntity = true
                gameScreen.inputHandler.linkingEntityCallback = {ent ->
                    if(ent != currentlySelectedEntity){
                        val building = Mappers.building[ent]
                        val selling = Mappers.selling[ent]

                        //TODO this needs to be more sophisticated
                        if(building.buildingType == BuildingComponent.BuildingType.Workshop)
                            comp.resellingEntityItemLinks.add(EntityListLink(ent, listOf(ItemPriceLink(selling.sellingItems[0].itemName, (selling.sellingItems[0].itemPrice * 1.5f).toInt()))))
                    }
                }
            }
        })
    }

    private fun setupSellingTable(table:Table, comp:SellingItemsComponent){
        val sellHistoryTable = Table()

        val sellLabel = Label("Selling: ${comp.sellingItems}", defaultLabelStyle)
        sellLabel.setFontScale(0.2f)

        table.add(sellLabel).expandX().fillX()
        table.row().expandX().fillX()
        table.add(sellHistoryTable).expandX().fillX()

        val title = Label("History:", defaultLabelStyle)
        title.setFontScale(0.25f)
        title.setAlignment(Align.center)
        val itemNameLabel = Label("Item", defaultLabelStyle)
        itemNameLabel.setFontScale(0.2f)
        itemNameLabel.setAlignment(Align.center)
        val itemAmountLabel = Label("Amt", defaultLabelStyle)
        itemAmountLabel.setFontScale(0.2f)
        itemAmountLabel.setAlignment(Align.center)
        val pricePerUnitLabel = Label("PPU", defaultLabelStyle)
        pricePerUnitLabel.setFontScale(0.2f)
        pricePerUnitLabel.setAlignment(Align.center)
        val timeStampLabel = Label("Time", defaultLabelStyle)
        timeStampLabel.setFontScale(0.2f)
        timeStampLabel.setAlignment(Align.center)
        val buyerNameLabel = Label("Buyer", defaultLabelStyle)
        buyerNameLabel.setFontScale(0.2f)
        buyerNameLabel.setAlignment(Align.center)

        val historyTableFunc = {
            sellHistoryTable.clear()

            sellHistoryTable.add(title).colspan(5).fillX().expandX()
            sellHistoryTable.row()
            sellHistoryTable.add(itemNameLabel).fillX().expandX()
            sellHistoryTable.add(itemAmountLabel).fillX().expandX()
            sellHistoryTable.add(pricePerUnitLabel).fillX().expandX()
            sellHistoryTable.add(timeStampLabel).fillX().expandX()
            sellHistoryTable.add(buyerNameLabel).fillX().expandX()
            sellHistoryTable.row()

            val limit = Math.max(0, comp.sellHistory.size - 5)

            for (i in (comp.sellHistory.size-1).downTo(limit)){
                val sell = comp.sellHistory[i]

                val _item = Label(sell.itemName, defaultLabelStyle)
                _item.setFontScale(0.2f)
                _item.setAlignment(Align.center)
                val _amount = Label(sell.itemAmount.toString(), defaultLabelStyle)
                _amount.setFontScale(0.2f)
                _amount.setAlignment(Align.center)
                val _ppu = Label(sell.pricePerItem.toString(), defaultLabelStyle)
                _ppu.setFontScale(0.2f)
                _ppu.setAlignment(Align.center)
                val _time = Label(sell.timeStamp.toString(), defaultLabelStyle)
                _time.setFontScale(0.2f)
                _time.setAlignment(Align.center)
                val _buyer = Label(sell.buyerName, defaultLabelStyle)
                _buyer.setFontScale(0.2f)
                _buyer.setAlignment(Align.center)

                sellHistoryTable.add(_item).fillX().expandX()
                sellHistoryTable.add(_amount).fillX().expandX()
                sellHistoryTable.add(_ppu).fillX().expandX()
                sellHistoryTable.add(_time).fillX().expandX()
                sellHistoryTable.add(_buyer).fillX().expandX()
                sellHistoryTable.row()
            }
        }

        historyTableFunc()
        updateMap.put("sellHistory", historyTableFunc)
        EventSystem.onEvent("guiUpdateSellHistory", {historyTableFunc()})

        updateList.add(UpdateLabel(sellLabel, { label -> label.setText("Selling: ${comp.sellingItems}")}))

        changedTabsFunc = {EventSystem.removeEvent("guiUpdateSellHistory")}
    }

    private class UpdateLabel(val label:Label, val updateFunc:(Label)->Unit){
        fun update(){
            updateFunc.invoke(label)
        }

    }
}