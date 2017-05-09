package com.quickbite.economy.gui

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.quickbite.economy.MyGame
import com.quickbite.economy.addChangeListener
import com.quickbite.economy.components.*
import com.quickbite.economy.event.EventSystem
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ItemSoldEvent
import com.quickbite.economy.gui.widgets.Graph
import com.quickbite.economy.interfaces.GUIWindow
import com.quickbite.economy.objects.SellingItemData
import com.quickbite.economy.util.Factory
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 3/9/2017.
 */
class EntityWindow(guiManager: GameScreenGUIManager, val entity:Entity) : GUIWindow(guiManager){
    private var currentlyDisplayingComponent: Component? = null
    private var currentlySelectedEntity: Entity? = null

    private var lastWorkerListCounter = 0

    init {
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

        val buildingLabel = TextButton("Building", defaultTextButtonStyle)
        buildingLabel.label.setFontScale(0.17f)

        val sellLabel = TextButton("Sell", defaultTextButtonStyle)
        sellLabel.label.setFontScale(0.17f)

        val workLabel = TextButton("Work", defaultTextButtonStyle)
        workLabel.label.setFontScale(0.17f)

        val behaviourLabel = TextButton("Beh", defaultTextButtonStyle)
        behaviourLabel.label.setFontScale(0.17f)

        val inventoryLabel = TextButton("Inv", defaultTextButtonStyle)
        inventoryLabel.label.setFontScale(0.17f)

        val buyingLabel = TextButton("Buying", defaultTextButtonStyle)
        buyingLabel.label.setFontScale(0.17f)

        val deleteLabel = TextButton("Delete", defaultTextButtonStyle)
        deleteLabel.label.setFontScale(0.17f)

        val exitLabel = TextButton("X", defaultTextButtonStyle)
        exitLabel.label.setFontScale(0.17f)

        if(bc != null)
            tabTable.add(buildingLabel).spaceRight(5f)
        if(sc != null)
            tabTable.add(sellLabel).spaceRight(5f)
        if(wc != null)
            tabTable.add(workLabel).spaceRight(5f)
        if(behComp != null)
            tabTable.add(behaviourLabel).spaceRight(5f)
        if(ic != null)
            tabTable.add(inventoryLabel).spaceRight(5f)
        if(buying != null)
            tabTable.add(buyingLabel).spaceRight(5f)

        tabTable.add(deleteLabel)

        tabTable.add().expandX().fillX()
        tabTable.add(exitLabel).right().width(32f)

        buildingLabel.addListener(object: ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(contentTable, bc)
                return true
            }
        })

        sellLabel.addListener(object: ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(contentTable, sc)
                return true
            }
        })

        workLabel.addListener(object: ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(contentTable, wc)
                return true
            }
        })

        behaviourLabel.addListener(object: ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(contentTable, behComp)
                return true
            }
        })

        inventoryLabel.addListener(object: ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(contentTable, ic)
                return true
            }
        })

        buyingLabel.addListener(object: ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(contentTable, buying)
                return true
            }
        })

        exitLabel.addListener(object: ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                close()
                return true
            }
        })

        deleteLabel.addListener(object:ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                close()
                Factory.destroyEntity(entity)
                return true
            }
        })

        //When we select a new building, try to display whatever section we were displaying on the last one
        if(currentlyDisplayingComponent != null)
            loadTable(contentTable, currentlyDisplayingComponent!!)
    }

    override fun update(delta:Float){
        updateList.forEach { it() }
    }

    override fun close() {
        super.close()

        //Prevent this crash....
        if(currentlySelectedEntity != null) {
            val debug = Mappers.debugDraw[currentlySelectedEntity]
            debug.debugDrawWorkers = false
            debug.debugDrawWorkplace = false
        }

        currentlySelectedEntity = null
    }

    private fun loadTable(table: Table, component: Component){
        table.clear()
        updateList.clear()

        val debug = Mappers.debugDraw[currentlySelectedEntity]
        debug.debugDrawWorkers = false
        debug.debugDrawWorkplace = false

        changedTabsFunc.invoke()

        when(component.javaClass){
            BuildingComponent::class.java -> setupBuildingTable(table, component as BuildingComponent)
            SellingItemsComponent::class.java -> setupSellingTable(table, component as SellingItemsComponent)
            WorkForceComponent::class.java -> {
                debug.debugDrawWorkers = true
                setupWorkforceTable(table, component as WorkForceComponent)
            }
            BehaviourComponent::class.java -> setupBehaviourTable(table, component as BehaviourComponent)
            InventoryComponent::class.java -> setupInventoryTable(table, component as InventoryComponent)
            BuyerComponent::class.java -> setupBuyingTable(table, component as BuyerComponent)
        }

        currentlyDisplayingComponent = component
    }

    private fun setupBuildingTable(table: Table, comp: BuildingComponent){
        val typeLabel = Label("Type: ${comp.buildingType}", defaultLabelStyle)
        typeLabel.setFontScale(0.2f)
        val queueLabel = Label("Queue size: ${comp.unitQueue.size}", defaultLabelStyle)
        queueLabel.setFontScale(0.2f)

        table.add(typeLabel).expandX().fillX()
        table.row()
        table.add(queueLabel).expandX().fillX()

        updateList.add({typeLabel.setText("Type: ${comp.buildingType}")})
        updateList.add({queueLabel.setText("Queue size: ${comp.unitQueue.size}")})
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

        //Our scroll pane
        val workerList = Table() //Our worker list

        val leftScrollPane = ScrollPane(workerList, scrollPaneStyle)

        //The main table area.
        val mainTableArea = Table()

        val workerTaskList = Table()
        val bottomScrollPane = ScrollPane(workerTaskList, scrollPaneStyle)

        val topInfoTable = Table()
        val mainTableWorkerInfo = Table()
        mainTableWorkerInfo.background = darkBackgroundDrawable

        val numWorkersLabel = Label("Workers: ${comp.workersAvailable.size}/${comp.numWorkerSpots}", defaultLabelStyle)
        numWorkersLabel.setFontScale(0.2f)

        val hireButton = TextButton("Hire", defaultTextButtonStyle)
        hireButton.label.setFontScale(0.2f)

        val tasksAndHireTable = Table()
        tasksAndHireTable.add(bottomScrollPane)
        tasksAndHireTable.add(hireButton)

        topInfoTable.add(numWorkersLabel)

        mainTableArea.add(topInfoTable)
        mainTableArea.row()
        mainTableArea.add(mainTableWorkerInfo).fill().expand().pad(5f, 5f, 5f, 0f)
        mainTableArea.row()
        mainTableArea.add(tasksAndHireTable)

        var selectedWorkerEntity: Entity? = null

        //The function to populate the main table where the worker and info are displayed
        val populateMainTableFunc = { workerEntity: Entity ->
            mainTableWorkerInfo.clear()
            val worker = Mappers.worker[workerEntity]!!
            val identity = Mappers.identity[workerEntity]

            val mainNameLabel = Label(identity.name, defaultLabelStyle)
            mainNameLabel.setFontScale(0.4f)

            val mainHappinessLabel = Label("Happiness: ${worker.happiness}", defaultLabelStyle)
            mainHappinessLabel.setFontScale(0.2f)

            val mainWageLabel = Label("Daily Wage: ${worker.dailyWage}", defaultLabelStyle)
            mainWageLabel.setFontScale(0.2f)

            val workHoursLabel = Label("Hours: ${worker.timeRange.first}-${worker.timeRange.second}", defaultLabelStyle)
            workHoursLabel.setFontScale(0.2f)

            val mainTasksLabel = Label(worker.taskList.joinToString(), defaultLabelStyle)
            mainTasksLabel.setFontScale(0.2f)


            mainTableWorkerInfo.add(mainNameLabel)
            mainTableWorkerInfo.row()
            mainTableWorkerInfo.add(mainHappinessLabel)
            mainTableWorkerInfo.row()
            mainTableWorkerInfo.add(mainWageLabel)
            mainTableWorkerInfo.row()
            mainTableWorkerInfo.add(workHoursLabel)
            mainTableWorkerInfo.row()
            mainTableWorkerInfo.add(mainTasksLabel)

            selectedWorkerEntity = workerEntity
        }

        //The function to populate the left side scrolling table where the list of workers are displayed
        val populateWorkerListScrollTable = {
            workerList.clear() //Clear the worker list from any leftover junk

            //Populate the left scrolling table
            comp.workersAvailable.forEach { entity ->
                val identity = Mappers.identity[entity]
                val worker = Mappers.worker[entity]

                val workerButton = Button(buttonStyle)

                val nameLabel = Label(identity.name, defaultLabelStyle)
                nameLabel.setFontScale(0.18f)

                var tasks = ""
                worker.taskList.forEach { task -> tasks += "${task[0].toUpperCase()}, " }

                val tasksLabel = Label(tasks, defaultLabelStyle)
                tasksLabel.setFontScale(0.12f)

                //Add the name and taskList to our worker table button
                workerButton.add(nameLabel)
                workerButton.row()
                workerButton.add(tasksLabel)

                //Add the worker table and a row (we want it to be a vertical list)
                workerList.add(workerButton).spaceTop(5f).width(80f)
                workerList.row()

                //When we click a worker, let's populate the main area with info
                workerButton.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        super.clicked(event, x, y)

                        populateMainTableFunc(entity)
                    }
                })
            }
        }

        //For the taskList available, list them
        comp.workerTasks.forEach { taskName ->
            val taskNameLabel = Label(taskName, defaultLabelStyle)
            taskNameLabel.setFontScale(0.2f)

            workerTaskList.add(taskNameLabel).space(0f, 5f, 0f, 5f)

            //When we click the task name label: if it's not owned by the worker, remove it. Otherwise, add it
            taskNameLabel.addListener(object:ClickListener(){
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    super.clicked(event, x, y)

                    //If the link is not null, lets do stuff
                    if(selectedWorkerEntity != null){
                        val taskNameText = taskNameLabel.text.toString()
                        val worker = Mappers.worker[selectedWorkerEntity]!!

                        //If it doesn't contain it, add it. Otherwise, remove it
                        if(!worker.taskList.contains(taskNameText))
                            worker.taskList.add(taskNameText)
                        else
                            worker.taskList.removeValue(taskNameText, false)

                        //We need to update the main table and worker list
                        populateWorkerListScrollTable()
                        populateMainTableFunc(selectedWorkerEntity!!)
                    }
                }
            })
        }

        hireButton.addListener(object:ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                guiManager.openHireWindow(this@EntityWindow.entity)
            }
        })

        populateWorkerListScrollTable()

        table.add(leftScrollPane).width(100f).expandY().fillY()
        table.add(mainTableArea).expand().fill()

        //Add a check to see if the worker list changed
        this.updateList.add {
            val size = comp.workersAvailable.size
            if(size != lastWorkerListCounter){
                lastWorkerListCounter = size
                populateWorkerListScrollTable()
            }
        }

        currentlyDisplayingComponent = comp
    }

    private fun setupBehaviourTable(table: Table, comp: BehaviourComponent){
        val taskLabel = Label("CurrTask: ${comp.currTask}", defaultLabelStyle)
        taskLabel.setFontScale(0.2f)
        taskLabel.setWrap(true)

        table.add(taskLabel).expandX().fillX()

        updateList.add({taskLabel.setText("CurrTask: ${comp.currTask}")})
    }

    private fun setupInventoryTable(table: Table, comp: InventoryComponent){

        val contentsTable = Table()
        contentsTable.background = darkBackgroundDrawable

        val amountLabel = Label("Amount of items: ${comp.itemMap.size}", defaultLabelStyle)
        amountLabel.setFontScale(0.2f)

        val listLabel = Label("Item List", defaultLabelStyle)
        listLabel.setFontScale(0.2f)
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

            comp.itemMap.values.forEach { (itemName, itemAmount) ->
                val itemLabel = Label(itemName, defaultLabelStyle)
                itemLabel.setFontScale(0.2f)
                itemLabel.setAlignment(Align.center)

                val itemAmountLabel = Label("$itemAmount", defaultLabelStyle)
                itemAmountLabel.setFontScale(0.2f)
                itemAmountLabel.setAlignment(Align.center)

                contentsTable.add(itemLabel).width(100f)
                contentsTable.add(itemAmountLabel).width(100f)
                contentsTable.row()
            }
        }

        populateItemTable()

        table.top()

        updateList.add({populateItemTable()})
//        updateList.add({listLabel.setText("Item List: ${comp.itemMap.values}")})
    }

    private fun setupBuyingTable(table: Table, comp: BuyerComponent){
        val buyingTable = Table()

        val itemNameTitle = Label("Item", defaultLabelStyle)
        itemNameTitle.setFontScale(0.25f)

        val itemAmountTitle = Label("Amount", defaultLabelStyle)
        itemAmountTitle.setFontScale(0.25f)

        val necessityRatingLabel = Label("Needs: ${comp.needsSatisfactionRating}", defaultLabelStyle)
        necessityRatingLabel.setFontScale(0.25f)

        val luxuryRatingLabel = Label("Luxury: ${comp.luxurySatisfactionRating}", defaultLabelStyle)
        luxuryRatingLabel.setFontScale(0.25f)

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
                itemName.setFontScale(0.2f)
                val itemAmount = Label("${pair.itemAmount}", defaultLabelStyle)
                itemAmount.setFontScale(0.2f)

                buyingTable.row()
                buyingTable.add(itemName)
                buyingTable.add(itemAmount)
            }
        }

        populateTableFunc()

        updateList.add {  populateTableFunc() }

        table.add(buyingTable)

//        updateList.add(UpdateLabel(nameLabel, { label -> label.setText("CurrTaskName: ${comp.currTaskName}")}))
    }

    private fun setupSellingTable(table: Table, comp: SellingItemsComponent){
        val graphStyle = Graph.GraphStyle(TextureRegionDrawable(TextureRegion(Util.createPixel(Color.BLACK))), Color.BLACK)
        graphStyle.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.GRAY)))
        graphStyle.graphBackground = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.ORANGE)))

        val taxRateTable = Table()
        taxRateTable.background = darkBackgroundDrawable

        val sellItemsMainTable = Table()
        sellItemsMainTable.background = darkBackgroundDrawable

        val sellHistoryTable = Table()
        sellHistoryTable.background = darkBackgroundDrawable

        //Set up the selling title
        val sellLabel = Label("Selling", defaultLabelStyle)
        sellLabel.setFontScale(0.25f)
        sellLabel.setAlignment(Align.center)

        //This will populate the table of items being sold
        val populateItemsTable = {
            sellItemsMainTable.clear()

            sellItemsMainTable.add(sellLabel)
            sellItemsMainTable.row()

            val sellItemsListTable = Table()

            val itemNameColTitle = Label("Name", defaultLabelStyle)
            itemNameColTitle.setFontScale(0.2f)

            val itemAmountColTitle = Label("Price", defaultLabelStyle)
            itemAmountColTitle.setFontScale(0.2f)

            val itemStockColTitle = Label("Stock", defaultLabelStyle)
            itemStockColTitle.setFontScale(0.2f)

            //Add the three titles
            sellItemsListTable.add(itemNameColTitle)
            sellItemsListTable.add(itemAmountColTitle)
            sellItemsListTable.add(itemStockColTitle)
            sellItemsListTable.add() //Empty spot for the X button
            sellItemsListTable.row()

            comp.currSellingItems.forEach { sellItemData ->
                //The item name
                val itemNameLabel = Label(sellItemData.itemName, defaultLabelStyle)
                itemNameLabel.setFontScale(0.2f)
                itemNameLabel.setAlignment(Align.center)

                //The item amount
                val itemAmountLabel = Label(sellItemData.itemPrice.toString(), defaultLabelStyle)
                itemAmountLabel.setFontScale(0.2f)
                itemAmountLabel.setAlignment(Align.center)

                /** The item stock amount */
                val itemStockTable = Table()

                val lessStockButton = TextButton("<", defaultTextButtonStyle)
                lessStockButton.label.setFontScale(0.15f)

                val moreStockButton = TextButton(">", defaultTextButtonStyle)
                moreStockButton.label.setFontScale(0.15f)

                fun getItemStockText():String =
                    if(sellItemData.itemStockAmount < 0) "max" else sellItemData.itemStockAmount.toString()

                val itemStockLabel = Label(getItemStockText(), defaultLabelStyle)
                itemStockLabel.setFontScale(0.2f)
//                itemStockLabel.style.font.data.scale(0.5f)

                itemStockTable.add(lessStockButton).size(32f)
                itemStockTable.add(itemStockLabel).space(0f, 5f, 0f, 5f)
                itemStockTable.add(moreStockButton).size(32f)

                lessStockButton.addListener(object:ClickListener(){
                    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                        super.touchUp(event, x, y, pointer, button)
                        sellItemData.itemStockAmount--
                        if(sellItemData.itemStockAmount < 0) sellItemData.itemStockAmount = -1

                        itemStockLabel.setText(getItemStockText())
                    }
                })

                moreStockButton.addListener(object:ClickListener(){
                    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                        super.touchUp(event, x, y, pointer, button)
                        sellItemData.itemStockAmount++
                        itemStockLabel.setText(getItemStockText())
                    }
                })


                //TODO Need listeners for the more/less stock buttons and need to restrict amounts...

                //The x Label if we want to delete the link from a store that is reselling
                val xLabel = TextButton("X", defaultTextButtonStyle)
                xLabel.label.setFontScale(0.15f)
                xLabel.label.setAlignment(Align.center)

                sellItemsListTable.add(itemNameLabel).width(100f)
                sellItemsListTable.add(itemAmountLabel).width(100f)
                sellItemsListTable.add(itemStockTable)
                if(comp.resellingItemsList.size > 0) sellItemsListTable.add(xLabel).size(16f).spaceLeft(10f).right() //Either add the x label
                else sellItemsListTable.add() //Or add an empty column
                sellItemsListTable.row()

                xLabel.addListener(object:ClickListener(){
                    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                        if(comp.resellingItemsList.size <= 0)
                            return
                        Util.removeSellingItemFromReseller(comp, sellItemData.itemName, sellItemData.itemSourceData)
                        super.touchUp(event, x, y, pointer, button)
                    }
                })
            }

            sellItemsMainTable.add(sellItemsListTable)
        }

        populateItemsTable()

        //--- Tax rate section ---

        val taxRateLabel = Label("${comp.taxRate}", defaultLabelStyle)
        taxRateLabel.setFontScale(0.2f)
        taxRateLabel.setAlignment(Align.center)

        val lessTaxButton = TextButton("<", defaultTextButtonStyle)
        lessTaxButton.label.setFontScale(0.2f)

        val moreTaxButton = TextButton(">", defaultTextButtonStyle)
        moreTaxButton.label.setFontScale(0.2f)

        taxRateTable.add(lessTaxButton).size(32f ,32f)
        taxRateTable.add(taxRateLabel).width(50f)
        taxRateTable.add(moreTaxButton).size(32f ,32f)

        //If we are also reselling, add this stuff
        if(comp.isReselling)
            setupResellingStuff(table, comp)

        //--- The titles for all the columns ---
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

        val goldHistoryGraph = Graph(mutableListOf(), 50, graphStyle)

        //The history table function. This will populate the table
        val historyTableFunc = {
            sellHistoryTable.clear()

            //Add all the titles
            sellHistoryTable.add(title).colspan(5).fillX().expandX()
            sellHistoryTable.row()
            sellHistoryTable.add(itemNameLabel).fillX().expandX()
            sellHistoryTable.add(itemAmountLabel).fillX().expandX()
            sellHistoryTable.add(pricePerUnitLabel).fillX().expandX()
            sellHistoryTable.add(timeStampLabel).fillX().expandX()
            sellHistoryTable.add(buyerNameLabel).fillX().expandX()
            sellHistoryTable.row()

            //TODO Don't use magic number to limit this size?
            //The limit of the history
            val limit = Math.max(0, comp.sellHistory.queue.size - 5)

            //For each history, set up the labels
            for (i in (comp.sellHistory.queue.size-1).downTo(limit)){
                val sell = comp.sellHistory.queue[i]

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

                //Add them all to the table and add a new row
                sellHistoryTable.add(_item).fillX().expandX()
                sellHistoryTable.add(_amount).fillX().expandX()
                sellHistoryTable.add(_ppu).fillX().expandX()
                sellHistoryTable.add(_time).fillX().expandX()
                sellHistoryTable.add(_buyer).fillX().expandX()
                sellHistoryTable.row()
            }
        }

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

        //Call the history table function to populate the history
        historyTableFunc()

        table.top()

        //Add all the stuff to the table
        table.add(taxRateTable).expandX().fillX().padTop(20f) //The taxCollected rate
        table.row().expandX().fillX().spaceTop(20f)
        table.add(sellItemsMainTable).expandX().fillX() //What we are selling
        table.row().expandX().fillX().spaceTop(20f)
        table.add(sellHistoryTable).expandX().fillX() //The history table
        table.row().expand().fill().spaceTop(20f) //Push everything up!
        table.add(goldHistoryGraph).size(350f, 250f)

        //Put the history function into our update map
        updateMap.put("sellHistory", historyTableFunc)

        updateList.add { populateItemsTable() }
        updateList.add { goldHistoryGraph.points = comp.goldHistory.queue.toList() }

        //Put in the event system
//        EventSystem.onEvent("guiUpdateSellHistory", {historyTableFunc()})

        val event = GameEventSystem.subscribe<ItemSoldEvent> { historyTableFunc() }

        changedTabsFunc = {
//            EventSystem.removeEvent("guiUpdateSellHistory")
            GameEventSystem.unsubscribe(event)
        }

//        goldHistoryGraph.debug = true
    }

    private fun setupResellingStuff(table: Table, comp: SellingItemsComponent){
        //Button style
        val buttonStyle = TextButton.TextButtonStyle()
        buttonStyle.up = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.CYAN))) as Drawable?
        buttonStyle.font = MyGame.manager["defaultFont", BitmapFont::class.java]
        buttonStyle.fontColor = Color.WHITE

        //The link button for linking together this shop and a store
        val linkButton = TextButton("Link", buttonStyle)
        linkButton.label.setFontScale(0.2f)

        val importButton = TextButton("Import", buttonStyle)
        importButton.label.setFontScale(0.2f)

        table.add(linkButton) //The link button
        table.row()
        table.add(importButton) //The import button
        table.row()

        //The listener for the link button
        linkButton.addListener(object: ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val selling = Mappers.selling[entity]

                //TODO Probably want to clean this up
                guiManager.gameScreen.inputHandler.linkingAnotherEntity = true
                guiManager.gameScreen.inputHandler.linkingEntityCallback = {ent ->
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
                                        val sellingItemData = SellingItemData(itemName, (itemPrice * 1.5).toInt(), -1, SellingItemData.ItemSource.Workshop, ent)
                                        comp.resellingItemsList.add(sellingItemData)
                                        if(!selling.currSellingItems.any{it.itemName == itemName})
                                            selling.currSellingItems.add(sellingItemData)
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
            println("Something")
            guiManager.openImportWindow(this.entity)
        }
    }
}