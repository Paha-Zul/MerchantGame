package com.quickbite.economy.gui

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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.MyGame
import com.quickbite.economy.addChangeListener
import com.quickbite.economy.components.*
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ItemSoldEvent
import com.quickbite.economy.event.events.ReloadGUIEvent
import com.quickbite.economy.gui.widgets.Graph
import com.quickbite.economy.gui.widgets.ProductionMap
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.objects.SelectedWorkerAndTable
import com.quickbite.economy.objects.SellingItemData
import com.quickbite.economy.util.Factory
import com.quickbite.economy.util.GUIUtil
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 3/9/2017.
 */
class EntityWindow(val entity:Entity) : GUIWindow(){

    private var currentlyDisplayingComponent: Component? = null
    private var currentlySelectedEntity: Entity? = null
    private var currentTabType: Int = 0
    private var selectedWorkers = Array<SelectedWorkerAndTable>()

    private var lastWorkerListCounter = 0

    init {
        val identity = Mappers.identity[entity]
        window.titleLabel.setText(identity.name)
        initEntityStuff()
    }

    private fun initEntityStuff(){
        currentlySelectedEntity = entity

        val sc = Mappers.selling.get(entity)
        val wc = Mappers.workforce.get(entity)
        val bc = Mappers.building.get(entity)
        val behComp = Mappers.behaviour.get(entity)
        val ic = Mappers.inventory.get(entity)
        val buying = Mappers.buyer.get(entity)
        val resource = Mappers.resource.get(entity)
        val production = Mappers.produces.get(entity)

        val buildingTab = TextButton("Building", defaultTextButtonStyle)
        buildingTab.label.setFontScale(1f)

        val sellTab = TextButton("Sell", defaultTextButtonStyle)
        sellTab.label.setFontScale(1f)

        val economyTab = TextButton("Eco", defaultTextButtonStyle)
        economyTab.label.setFontScale(1f)

        val workTab = TextButton("Work", defaultTextButtonStyle)
        workTab.label.setFontScale(1f)

        val behaviourTab = TextButton("Beh", defaultTextButtonStyle)
        behaviourTab.label.setFontScale(1f)

        val inventoryTab = TextButton("Inv", defaultTextButtonStyle)
        inventoryTab.label.setFontScale(1f)

        val buyingLabel = TextButton("Buying", defaultTextButtonStyle)
        buyingLabel.label.setFontScale(1f)

        val resourceTab = TextButton("Resource", defaultTextButtonStyle)

        val productionTab = TextButton("Production", defaultTextButtonStyle)

        val deleteLabel = TextButton("Delete", defaultTextButtonStyle)
        deleteLabel.label.setFontScale(1f)

        tabTable.defaults().width(60f).height(TAB_HEIGHT)

        if(bc != null)
            tabTable.add(buildingTab).spaceRight(5f)
        if(sc != null) {
            tabTable.add(sellTab).spaceRight(5f)
            tabTable.add(economyTab).spaceRight(5f)
        }
        if(wc != null)
            tabTable.add(workTab).spaceRight(5f)
        if(behComp != null)
            tabTable.add(behaviourTab).spaceRight(5f)
        if(ic != null)
            tabTable.add(inventoryTab).spaceRight(5f)
        if(buying != null)
            tabTable.add(buyingLabel).spaceRight(5f)
        if(resource != null)
            tabTable.add(resourceTab).spaceRight(5f)
        if(production != null)
            tabTable.add(productionTab).spaceRight(5f)

        tabTable.add().width(0f).growX()

        tabTable.add(deleteLabel).right()

        buildingTab.addListener(object: ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                loadTable(contentTable, bc, 1)
            }
        })

        sellTab.addListener(object: ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                loadTable(contentTable, sc, 2)
            }
        })

        economyTab.addListener(object: ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                loadTable(contentTable, sc, 3)
            }
        })


        workTab.addListener(object: ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                loadTable(contentTable, wc, 4)
            }
        })

        behaviourTab.addListener(object: ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                loadTable(contentTable, behComp, 5)
            }
        })

        inventoryTab.addListener(object: ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                loadTable(contentTable, ic, 6)
            }
        })

        buyingLabel.addListener(object: ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                loadTable(contentTable, buying, 7)
            }
        })

        resourceTab.addListener(object: ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                loadTable(contentTable, resource, 8)
            }
        })

        productionTab.addListener(object: ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                loadTable(contentTable, production, 9)
            }
        })

        deleteLabel.addListener(object:ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                close()
                Factory.destroyEntity(entity)
            }
        })

        //When we select a new building, try to display whatever section we were displaying on the last one
        if(currentlyDisplayingComponent != null)
            loadTable(contentTable, currentlyDisplayingComponent!!, currentTabType)
    }

    override fun update(delta:Float){
        super.update(delta)
    }

    override fun close() {
        super.close()

        //Prevent this crash....
        if(currentlySelectedEntity != null) {
            val debug = Mappers.debugDraw[currentlySelectedEntity]
            if(debug != null) {
                debug.debugDrawWorkers = false
                debug.debugDrawWorkplace = false
            }
        }

        currentlySelectedEntity = null
    }

    private fun loadTable(table: Table, component: Component, tabType:Int){
        table.clear()
        updateFuncsList.clear()

        val debug = Mappers.debugDraw[currentlySelectedEntity]
        debug.debugDrawWorkers = false
        debug.debugDrawWorkplace = false

        changedTabsFunc() //Call the change tabs function
        changedTabsFunc = {} //Clear the change tabs function

        when(tabType){
            1 -> setupBuildingTable(table, component as BuildingComponent)
            2 -> setupSellingTable(table, component as SellingItemsComponent)
            3 -> setupEconomyTable(table, component as SellingItemsComponent)
            4 -> {
                debug.debugDrawWorkers = true
                setupWorkforceTable(table, component as WorkForceComponent)
            }
            5 -> setupBehaviourTable(table, component as BehaviourComponent)
            6 -> setupInventoryTable(table, component as InventoryComponent)
            7 -> setupBuyingTable(table, component as BuyerComponent)
            8 -> setupResourceTable(table, component as ResourceComponent)
            9 -> setupProductionTable(table, component as ProduceItemComponent)
        }

        currentlyDisplayingComponent = component
        currentTabType = tabType
    }

    private fun setupBuildingTable(table: Table, comp: BuildingComponent){
        val typeLabel = Label("Type: ${comp.buildingType}", defaultLabelStyle)
        typeLabel.setFontScale(1f)
        val queueLabel = Label("Queue size: ${comp.unitQueue.size}", defaultLabelStyle)
        queueLabel.setFontScale(1f)

        table.add(typeLabel).expandX().fillX()
        table.row()
        table.add(queueLabel).expandX().fillX()

        updateFuncsList.add({typeLabel.setText("Type: ${comp.buildingType}")})
        updateFuncsList.add({queueLabel.setText("Queue size: ${comp.unitQueue.size}")})
    }

    private fun setupWorkforceTable(table: Table, comp: WorkForceComponent){
        val scrollPaneStyle = ScrollPane.ScrollPaneStyle()
        scrollPaneStyle.background = darkBackgroundDrawable
        scrollPaneStyle.vScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        scrollPaneStyle.vScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color(Color.BLACK))))
        scrollPaneStyle.hScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        scrollPaneStyle.hScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color(Color.BLACK))))

        val buttonStyle = Button.ButtonStyle()
        buttonStyle.up = buttonBackgroundDrawable

        //Our worker list table
        val workerListTable = Table()
        workerListTable.top()

        //Our scroll pane for the worker list table to sit inside
        val workerListScrollPane = ScrollPane(workerListTable, scrollPaneStyle)
        workerListScrollPane.setScrollingDisabled(true, false)

        //The worker tasks and amounts table
        val workerTasksAndAmountsTables = Table()

        GUIUtil.populateWorkerTasksAndAmountsTable(comp, workerTasksAndAmountsTables, defaultLabelStyle)
        GUIUtil.populateWorkerTable(comp, selectedWorkers, workerListTable, defaultLabelStyle, defaultTextButtonStyle, GameScreenGUIManager)

        val workerTaskList = Table()

        /** This sets the tasks that are available to set to each worker */
        comp.workerTasksLimits.forEach { (taskName, amount) ->
            val taskNameLabel = Label(taskName, defaultLabelStyle)
            taskNameLabel.setFontScale(1f)

            workerTaskList.add(taskNameLabel).space(0f, 5f, 0f, 5f)

            //When we click the task name label: if it's not owned by the worker, remove it. Otherwise, add it
            taskNameLabel.addListener(object:ClickListener(){
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    super.clicked(event, x, y)
                    val taskNameText = taskNameLabel.text.toString()
                    val workerTaskLimit = comp.workerTasksLimits.find { it.taskName == taskNameText}!!

                    EntityWindowController.addTaskToWorkers(taskNameText, selectedWorkers, currentlySelectedEntity!!)

                    GUIUtil.populateWorkerTable(comp, selectedWorkers, workerListTable, defaultLabelStyle, defaultTextButtonStyle, GameScreenGUIManager)
                }
            })
        }

        //The button to open the hire window
        val hireButton = TextButton("Hire", defaultTextButtonStyle)
        hireButton.label.setFontScale(1f)

        //Hire button listener
        hireButton.addListener(object:ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                GameScreenGUIManager.openHireWindow(this@EntityWindow.entity)
            }
        })

        //An event to listen for a worker to be hired
        val updateEvent = GameEventSystem.subscribe<ReloadGUIEvent> { GUIUtil.populateWorkerTable(comp, selectedWorkers, workerListTable, defaultLabelStyle, defaultTextButtonStyle, GameScreenGUIManager) }

        //Remember to remove this from the event system
        changedTabsFunc = { GameEventSystem.unsubscribe(updateEvent) }

        //Add our main scroll pane and the hiring button
        table.add(workerTasksAndAmountsTables).colspan(2).spaceBottom(5f)
        table.row()
        table.add(workerListScrollPane).grow().top().colspan(2).height(225f)
        table.row()
        table.add(workerTaskList)
        table.add(hireButton)
    }

    private fun setupBehaviourTable(table: Table, comp: BehaviourComponent){
        val taskLabel = Label("CurrTask: ${comp.currTask}", defaultLabelStyle)
        taskLabel.setFontScale(1f)
        taskLabel.setWrap(true)

        table.add(taskLabel).expandX().fillX()

        updateFuncsList.add({taskLabel.setText("CurrTask: ${comp.currTask}")})
    }

    private fun setupInventoryTable(table: Table, comp: InventoryComponent){
        val contentsTable = Table()
        contentsTable.background = darkBackgroundDrawable

        val amountLabel = Label("Amount of items: ${comp.itemMap.size}", defaultLabelStyle)
        amountLabel.setFontScale(1f)

        val listLabel = Label("Item List", defaultLabelStyle)
        listLabel.setFontScale(1f)
        listLabel.setAlignment(Align.center)

        //The main table...
        table.add(amountLabel).expandX().fillX()
        table.row()
        table.add(contentsTable).expandX().fillX()
        table.row()

        val populateItemTable = {
            contentsTable.clear()
            //The contents table
            contentsTable.add(listLabel).colspan(2)
            contentsTable.row()

            println("populating")

            comp.itemMap.values.forEach { (itemName, itemAmount) ->
                val itemName = itemName.toLowerCase()
                val iconName = DefinitionManager.itemDefMap[itemName]?.iconName ?: ""
                val iconImage = Image(TextureRegion(MyGame.manager[iconName, Texture::class.java]))

                val itemAmountLabel = Label("$itemAmount", defaultLabelStyle)
                itemAmountLabel.setFontScale(1f)
                itemAmountLabel.setAlignment(Align.center)

//                println("icon images")

                iconImage.addListener(object: ClickListener(){
                    override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                        super.enter(event, x, y, pointer, fromActor)
                        GUIUtil.makeSimpleLabelTooltip(itemName)
                        GameScreenGUIManager.startShowingTooltip(GameScreenGUIManager.TooltipLocation.Mouse)
                    }

                    override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                        super.exit(event, x, y, pointer, toActor)
                        GameScreenGUIManager.stopShowingTooltip()
                    }
                })

                contentsTable.add(iconImage).size(24f)
                contentsTable.add(itemAmountLabel).width(100f)
                contentsTable.row().padTop(2f)
            }
        }

        populateItemTable()

        table.top()

        val listener = comp.addInventoryListener("all", {_,_,_ -> populateItemTable()})

//        updateFuncsList.add({populateItemTable()})

        changedTabsFunc = {
            comp.removeInventoryListener("all", listener)
        }
    }

    private fun setupProductionTable(table: Table, comp: ProduceItemComponent){
        val contentsTable = Table()
        contentsTable.background = darkBackgroundDrawable

        contentsTable.add(ProductionMap(comp))

        table.add(contentsTable)
        table.left()
    }

    private fun setupBuyingTable(table: Table, comp: BuyerComponent){
        val buyingTable = Table()

        val itemNameTitle = Label("Item", defaultLabelStyle)
        itemNameTitle.setFontScale(1f)

        val itemAmountTitle = Label("Amount", defaultLabelStyle)
        itemAmountTitle.setFontScale(1f)

        val necessityRatingLabel = Label("Needs: ${comp.needsSatisfactionRating}", defaultLabelStyle)
        necessityRatingLabel.setFontScale(1f)

        val luxuryRatingLabel = Label("Luxury: ${comp.luxurySatisfactionRating}", defaultLabelStyle)
        luxuryRatingLabel.setFontScale(1f)

        val populateTableFunc = {
            buyingTable.clear()

            luxuryRatingLabel.setText("Luxury: ${comp.luxurySatisfactionRating}")
            necessityRatingLabel.setText("Needs: ${comp.needsSatisfactionRating}")

            buyingTable.add(necessityRatingLabel)
            buyingTable.add(luxuryRatingLabel)
            buyingTable.row()
            buyingTable.add(itemNameTitle)
            buyingTable.add(itemAmountTitle)

            comp.buyList.forEach { pair ->
                val itemName = Label(pair.itemName, defaultLabelStyle)
                itemName.setFontScale(1f)
                val itemAmount = Label("${pair.itemAmount}", defaultLabelStyle)
                itemAmount.setFontScale(1f)

                buyingTable.row()
                buyingTable.add(itemName)
                buyingTable.add(itemAmount)
            }
        }

        populateTableFunc()

        updateFuncsList.add {  populateTableFunc() }

        table.add(buyingTable)

//        updateFuncsList.add(UpdateLabel(nameLabel, { label -> label.setText("CurrTaskName: ${comp.currTaskName}")}))
    }

    private fun setupSellingTable(table: Table, comp: SellingItemsComponent){
        val taxRateTable = Table()
        taxRateTable.background = darkBackgroundDrawable

        val sellItemsMainTable = Table()
        sellItemsMainTable.background = darkBackgroundDrawable

        val sellHistoryTable = Table()
        sellHistoryTable.background = darkBackgroundDrawable

        //Tax title
        val taxLabel = Label("Tax", this.defaultTitleLabelStyle)
        taxLabel.setAlignment(Align.center)

        //Set up the selling title
        val sellLabel = Label("Selling", this.defaultTitleLabelStyle)
        sellLabel.setAlignment(Align.center)

        val historyTitleLabel = Label("History", defaultTitleLabelStyle)
        historyTitleLabel.setAlignment(Align.center)

        //This will populate the table of items being sold

        //Initially call this function to populate the items table
        GUIUtil.populateItemsTable(comp, sellItemsMainTable, defaultLabelStyle, defaultTextButtonStyle)

        /**--- Tax rate section --- **/

        val taxRateLabel = Label("${comp.taxRate}", defaultLabelStyle)
        taxRateLabel.setFontScale(1f)
        taxRateLabel.setAlignment(Align.center)

        val lessTaxButton = TextButton("-", defaultTextButtonStyle)
        lessTaxButton.label.setFontScale(1f)

        val moreTaxButton = TextButton("+", defaultTextButtonStyle)
        moreTaxButton.label.setFontScale(1f)

        taxRateTable.add(lessTaxButton).size(16f)
        taxRateTable.add(taxRateLabel).width(50f)
        taxRateTable.add(moreTaxButton).size(16f)

        //If we are also reselling, add this stuff
        if(comp.isReselling)
            setupResellingStuff(table, comp)

        //--- The titles for all the columns ---

        //The listener for when we hit the less tax button
        lessTaxButton.addListener(object:ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                //The 0.5 amd 4.5 are because of rounding for bad floats
                var adj = 0.5
                if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                    adj = 4.5

                var rate = (comp.taxRate*100 - adj).toInt()
                if(rate < 0)
                    rate = 0

                comp.taxRate = rate/100f

                taxRateLabel.setText("${comp.taxRate}")
            }
        })

        //The callback for hitting the more tax button
        moreTaxButton.addListener(object:ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                //The 1.5 amd 5.5 are because of rounding for bad floats
                var adj = 1.5
                if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                    adj = 5.5

                var rate = (comp.taxRate*100 + adj).toInt()
                if(rate > 100)
                    rate = 100

                comp.taxRate = rate/100f

                taxRateLabel.setText("${comp.taxRate}")
            }
        })

        table.top()

        //Add all the stuff to the table
        table.add(taxLabel).growX().padTop(5f) //The taxCollected rate
        table.row().growX().spaceTop(0f)
        table.add(taxRateTable).growX().padTop(5f) //The taxCollected rate
        table.row().growX().spaceTop(5f)
        table.add(sellLabel).growX() //What we are selling
        table.row().growX().spaceTop(5f)
        table.add(sellItemsMainTable).growX() //What we are selling
        table.row().growX().spaceTop(5f)
        table.add(historyTitleLabel).growX() //The history table
        table.row().grow().spaceTop(5f) //Push everything up!
        table.add(sellHistoryTable).growX() //The history table
        table.row().grow().spaceTop(5f) //Push everything up!

        table.invalidateHierarchy()
        table.validate()
        table.layout()
        table.act(0.016f)

        //Call the history table function to populate the history
        GUIUtil.populateHistoryTable(comp, sellHistoryTable, defaultLabelStyle, table.width)

        //Put the history function into our update map
        updateMap.put("sellHistory", {GUIUtil.populateHistoryTable(comp, sellHistoryTable, defaultLabelStyle, table.width)})

        updateFuncsList.add { GUIUtil.populateItemsTable(comp, sellItemsMainTable, defaultLabelStyle, defaultTextButtonStyle) }

        //Put in the event system
        val entID = Mappers.identity[currentlySelectedEntity].uniqueID
        val entityEvent = GameEventSystem.subscribe<ItemSoldEvent>({GUIUtil.populateHistoryTable(comp, sellHistoryTable, defaultLabelStyle, table.width)}, entID) //Subscribe to the entity selling an item

        changedTabsFunc = {
            GameEventSystem.unsubscribe(entityEvent) //Unsubscribe when we leave this tab
        }
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

    private fun setupResellingStuff(table: Table, comp: SellingItemsComponent){
        //The link button for linking together this shop and a store
        val linkButton = TextButton("Link", defaultTextButtonStyle)
        val importButton = TextButton("Import", defaultTextButtonStyle)

        val buttonTable = Table()
        buttonTable.add(linkButton).spaceRight(50f) //The link button
        buttonTable.add(importButton) //The import button

        table.add(buttonTable).padTop(20f)
        table.row()

        //The listener for the link button
        linkButton.addListener(object: ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                //TODO Probably want to clean this up
                GameScreenGUIManager.gameScreen.inputHandler.linkingAnotherEntity = true
                GameScreenGUIManager.gameScreen.inputHandler.linkingEntityCallback = {ent ->
                    if(ent != currentlySelectedEntity){
                        val otherBuilding = Mappers.building[ent]
                        val otherSelling = Mappers.selling[ent]

                        //If we aren't linking to a building, then don't do this...
                        if(otherBuilding != null) {
                            //TODO this needs to be more sophisticated, maybe remove the selling potential of the workshop?
                            if (otherBuilding.buildingType == BuildingComponent.BuildingType.Workshop) {
                                //Make sure we actually have stuff to add
                                if (otherSelling.currSellingItems.size > 0) {

                                    otherSelling.currSellingItems.forEach { (itemName, itemPrice) ->
                                        Util.addItemToEntityReselling(entity, itemName, SellingItemData.ItemSource.Workshop, ent)
                                    }

                                    otherSelling.currSellingItems.clear()
                                }
                            }
                        }
                    }
                }
            }
        })

        importButton.addChangeListener { _, _ ->
            GameScreenGUIManager.openImportWindow(this.entity)
        }
    }

    private fun setupResourceTable(table: Table, comp: ResourceComponent){
        table.top().left()

        val resourceTypeLabel = Label(comp.resourceType, defaultLabelStyle)
    }
}