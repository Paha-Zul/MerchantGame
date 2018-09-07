package com.quickbite.economy.gui.entity

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.utils.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.MyGame
import com.quickbite.economy.addChangeListener
import com.quickbite.economy.components.*
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ItemSoldEvent
import com.quickbite.economy.event.events.ReloadGUIEvent
import com.quickbite.economy.gui.EntityWindowController
import com.quickbite.economy.gui.GUIUtil
import com.quickbite.economy.gui.GUIWindow
import com.quickbite.economy.gui.GameScreenGUIManager
import com.quickbite.economy.gui.widgets.Graph
import com.quickbite.economy.gui.widgets.ProductionMap
import com.quickbite.economy.input.InputController
import com.quickbite.economy.interfaces.IEntCompWindow
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.systems.RenderSystem
import com.quickbite.economy.util.Factory
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util
import com.quickbite.economy.util.objects.SelectedWorkerAndTable

/**
 * Created by Paha on 3/9/2017.
 */
class EntityWindow(val entity:Entity) : GUIWindow(){
    private enum class TabType{BuildingTab, SellTab, EconomyTab, WorkTab, BehaviourTab,
        InventoryTab, FarmTab, BuyingTab, ResourceTab, ProductionTab, BuyerTab, DeleteTab}

    private var currentlyDisplayingComponent: Component? = null
    private var currentTabType: TabType = TabType.BuildingTab
    private var selectedWorkers = Array<SelectedWorkerAndTable>()

    private var lastWorkerListCounter = 0

    private var openCompWindow:IEntCompWindow? = null

    init {
        val identity = Mappers.identity[entity]
        window.titleLabel.setText(identity.name)
        initEntityStuff()
    }

    private fun initEntityStuff(){
        val buttonStyle = TextButton.TextButtonStyle()
        buttonStyle.font = MyGame.defaultFont14
        buttonStyle.fontColor = Color.WHITE
        buttonStyle.checked = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.valueOf("565244ff"))))

        val sc = Mappers.selling.get(entity)
        val wc = Mappers.workforce.get(entity)
        val bc = Mappers.building.get(entity)
        val behComp = Mappers.behaviour.get(entity)
        val ic = Mappers.inventory.get(entity)
        val buying = Mappers.buyer.get(entity)
        val resource = Mappers.resource.get(entity)
        val production = Mappers.produces.get(entity)
        val fc = Mappers.farm.get(entity)

        val buildingTab = TextButton("Building", buttonStyle)
        val sellTab = TextButton("Sell", buttonStyle)

        val economyTab = TextButton("Eco", buttonStyle)
        val workTab = TextButton("Work", buttonStyle)
        val behaviourTab = TextButton("Beh", buttonStyle)
        val inventoryTab = TextButton("Inv", buttonStyle)
        val farmTab = TextButton("Farm", buttonStyle)
        val buyingTab = TextButton("Buying", buttonStyle)
        val resourceTab = TextButton("Resource", buttonStyle)
        val productionTab = TextButton("Prod", buttonStyle)
        val deleteButton = TextButton("Delete", buttonStyle)

        tabTable.defaults().minWidth(60f)
        tabTable.background.topHeight = 0f
        tabTable.background.bottomHeight = 0f
        tabTable.background.leftWidth = 0f
        tabTable.background.rightWidth = 0f

        if(bc != null)
            addTab(bc, buildingTab, tabTable, TabType.BuildingTab)
        if(sc != null) {
            addTab(sc, sellTab, tabTable, TabType.SellTab)
            addTab(sc, economyTab, tabTable, TabType.EconomyTab)
        }
        if(wc != null)
            addTab(wc, workTab, tabTable, TabType.WorkTab)
        if(behComp != null)
            addTab(behComp, behaviourTab, tabTable, TabType.BehaviourTab)
        if(ic != null)
            addTab(ic, inventoryTab, tabTable, TabType.InventoryTab)
        if(buying != null)
            addTab(buying, buyingTab, tabTable, TabType.BuyerTab)
        if(resource != null)
            addTab(resource, resourceTab, tabTable, TabType.ResourceTab)
        if(fc != null)
            addTab(fc, farmTab, tabTable, TabType.FarmTab) //TODO Problem here?!
        if(production != null && production.productionList.size != 0)
            addTab(production, productionTab, tabTable, TabType.ProductionTab)

        tabTable.add().width(0f).growX()

        tabTable.add(deleteButton).right()

        deleteButton.addListener(object:ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                close()
                Factory.destroyEntity(entity)
            }
        })

        //When we select a new building, try to display whatever section we were displaying on the last one
        if(currentlyDisplayingComponent != null)
            loadTable(contentTable, currentlyDisplayingComponent!!, currentTabType)

        val group = ButtonGroup<TextButton>(buildingTab, sellTab, economyTab, workTab, behaviourTab,
                inventoryTab, buyingTab, resourceTab, productionTab, farmTab, deleteButton)

        group.setMaxCheckCount(1)
    }

    private fun <T> addTab(comp:T, tab:TextButton, tabTable:Table, tabType: TabType,
                           func:()->Unit = {loadTable(contentTable, comp, tabType)}) where T:Component{
        tabTable.add(tab).spaceRight(5f).growY()
        tab.addListener(object:ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                func()
            }
        })
    }

    override fun update(delta:Float){
        super.update(delta)
    }

    override fun close() {
        super.close()

        //Prevent this crash....
        val debug = Mappers.debugDraw[entity]
        if(debug != null) {
            debug.debugDrawWorkers = false
            debug.debugDrawWorkplace = false
        }
    }

    private fun <T> loadTable(table: Table, component: T, tabType: TabType) where T:Component{
        table.clear()
        updateFuncsList.clear()

        val debug = Mappers.debugDraw[entity]
        debug.debugDrawWorkers = false
        debug.debugDrawWorkplace = false

        changedTabsFunc() //Call the change tabs function
        changedTabsFunc = {} //Clear the change tabs function

        when(tabType){
            TabType.BuildingTab -> setupBuildingTable(table, component as BuildingComponent)
            TabType.SellTab -> setupSellingTable(table, component as SellingItemsComponent)
            TabType.EconomyTab -> setupEconomyTable(table, component as SellingItemsComponent)
            TabType.WorkTab -> {
                debug.debugDrawWorkers = true
                setupWorkforceTable(table, component as WorkForceComponent)
            }
            TabType.BehaviourTab -> setupBehaviourTable(table, component as BehaviourComponent)
            TabType.InventoryTab -> setupInventoryTable(table, component as InventoryComponent)
            TabType.BuyerTab -> setupBuyingTable(table, component as BuyerComponent)
            TabType.ResourceTab -> setupResourceTable(table, component as ResourceComponent)
            TabType.ProductionTab -> setupProductionTable(table, component as ProduceItemComponent)
            TabType.FarmTab -> setupFarmTable(table, component as FarmComponent)
        }

        currentlyDisplayingComponent = component
        currentTabType = tabType
    }

    private fun setupBuildingTable(table: Table, comp: BuildingComponent){
        openCompWindow = EntBuildingWindow()
        openCompWindow!!.open(this, comp, table)
    }

    private fun setupFarmTable(table:Table, comp:FarmComponent){
        openCompWindow = EntFarmingWindow()
        openCompWindow!!.open(this, comp, table)
    }

    private fun setupWorkforceTable(table: Table, comp: WorkForceComponent){
        openCompWindow = EntWorkforceWindow()
        openCompWindow!!.open(this, comp, table)
    }

    private fun setupBehaviourTable(table: Table, comp: BehaviourComponent){
        val taskLabel = Label("CurrTask: ${comp.currTask}", defaultLabelStyle)
        taskLabel.setFontScale(1f)
        taskLabel.setWrap(true)

        table.add(taskLabel).expandX().fillX()

        updateFuncsList.add {taskLabel.setText("CurrTask: ${comp.currTask}")}
    }

    private fun setupInventoryTable(table: Table, comp: InventoryComponent){
        openCompWindow = EntInventoryWindow()
        openCompWindow!!.open(this, comp, table)
    }

    private fun setupProductionTable(table: Table, comp: ProduceItemComponent){
        val contentsTable = Table()
        contentsTable.background = darkBackgroundDrawable

        contentsTable.add(ProductionMap(comp))

        table.add(contentsTable)
        table.left()
    }

    private fun setupBuyingTable(table: Table, comp: BuyerComponent){
        openCompWindow = EntBuyingWindow()
        openCompWindow!!.open(this, comp, table)
    }

    private fun setupSellingTable(table: Table, comp: SellingItemsComponent){
        openCompWindow = EntSellingWindow()
        openCompWindow!!.open(this, comp, table)
    }

    private fun setupEconomyTable(table: Table, comp: SellingItemsComponent){
        val graphStyle = Graph.GraphStyle(TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE))), Color.WHITE, MyGame.defaultFont14)
        graphStyle.graphBackground = NinePatchDrawable(NinePatch(MyGame.manager["graphBackground", Texture::class.java], 7, 7, 7, 7))

        val darkLabelStyle = Label.LabelStyle(MyGame.defaultFont14, Color.WHITE)
        darkLabelStyle.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.DARK_GRAY)))

        val incomeDailyTitleLabel = Label("Daily Income", defaultLabelStyle)

        val incomeDailyLabel = Label("${comp.incomeDaily}", darkLabelStyle)
        incomeDailyLabel.setAlignment(Align.center)

        val taxDailyTileLabel = Label("Daily Tax", defaultLabelStyle)
        val taxDailyLabel = Label("${comp.taxCollectedDaily}", darkLabelStyle)
        taxDailyLabel.setAlignment(Align.center)

        val incomeTotalTitleLabel = Label("Total Income", defaultLabelStyle)
        val incomeTotalLabel = Label("${comp.incomeTotal}", darkLabelStyle)
        incomeTotalLabel.setAlignment(Align.center)

        val taxTotalTitleLabel = Label("Total Tax", defaultLabelStyle)
        val taxTotalLabel = Label("${comp.taxCollectedTotal}", darkLabelStyle)
        taxTotalLabel.setAlignment(Align.center)

        val goldHistoryGraph = Graph(mutableListOf(), 50, graphStyle)

        val incomeTable = Table()

        incomeTable.add(incomeDailyTitleLabel).spaceRight(20f).width(75f).uniformX().fillX()
        incomeTable.add(incomeDailyLabel).width(50f)
        incomeTable.add().growX()
        incomeTable.row()
        incomeTable.add(taxDailyTileLabel).spaceRight(20f).uniformX().fillX()
        incomeTable.add(taxDailyLabel).width(50f)
        incomeTable.add().growX()
        incomeTable.row()
        incomeTable.add(incomeTotalTitleLabel).spaceRight(20f).uniformX().fillX()
        incomeTable.add(incomeTotalLabel).width(50f)
        incomeTable.add().growX()
        incomeTable.row()
        incomeTable.add(taxTotalTitleLabel).spaceRight(20f).uniformX().fillX()
        incomeTable.add(taxTotalLabel).width(50f)
        incomeTable.add().growX()


        table.add(incomeTable).left().expandX().fillX()
        table.row()
        table.add(goldHistoryGraph).size(350f, 250f).padTop(20f)

        table.top()

        //Keep these labels updated while this UI view is open
        updateFuncsList.add {
            incomeDailyLabel.setText("${comp.incomeDaily}")
            taxDailyLabel.setText("${comp.taxCollectedDaily}")
            incomeTotalLabel.setText("${comp.incomeTotal}")
            taxTotalLabel.setText("${comp.taxCollectedTotal}")

            goldHistoryGraph.points = comp.goldHistory.queue.toList()
        }

    }

    private fun setupResourceTable(table: Table, comp: ResourceComponent){
        table.top().left()

        val resourceTypeLabel = Label(comp.resourceType, defaultLabelStyle)
    }
}