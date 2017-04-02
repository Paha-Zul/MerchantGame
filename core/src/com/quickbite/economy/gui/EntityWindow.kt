package com.quickbite.economy.gui

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.*
import com.quickbite.economy.interfaces.GuiWindow
import com.quickbite.economy.util.*
import com.quickbite.spaceslingshot.util.EventSystem
import java.util.*

/**
 * Created by Paha on 3/9/2017.
 */
class EntityWindow(val guiManager: GameScreenGUIManager, val entity:Entity) : GuiWindow{
    private val window:Window

    private val updateList: Array<() -> Unit> = Array(5)
    private val updateMap: HashMap<String, () -> Unit> = hashMapOf()
    private var changedTabsFunc:()->Unit = {}

    private var currentlyDisplayingComponent: Component? = null

    private var currentlySelectedEntity: Entity? = null

    val defaultLabelStyle = Label.LabelStyle(MyGame.manager["defaultFont", BitmapFont::class.java], Color.WHITE)
    val mainTable = Table()

    val defaultButtonStyle = TextButton.TextButtonStyle()
    val darkBackgroundDrawable = NinePatchDrawable(NinePatch(MyGame.manager["dark_bar", Texture::class.java], 10, 10, 10, 10))
    val buttonBackgroundDrawable = NinePatchDrawable(NinePatch(MyGame.manager["button", Texture::class.java], 10, 10, 10, 10))

    init {
        defaultButtonStyle.up = NinePatchDrawable(NinePatch(MyGame.manager["button", Texture::class.java], 10, 10, 10, 10))
        defaultButtonStyle.font = MyGame.manager["defaultFont", BitmapFont::class.java]
        defaultButtonStyle.fontColor = Color.WHITE

        currentlySelectedEntity = entity

//        mainTable.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE, 400, 600)))

        val tabTable = Table()
        tabTable.background = darkBackgroundDrawable

        val contentTable = Table()

        val sc = Mappers.selling.get(entity)
        val wc = Mappers.workforce.get(entity)
        val bc = Mappers.building.get(entity)
        val behComp = Mappers.behaviour.get(entity)
        val ic = Mappers.inventory.get(entity)
        val buying = Mappers.buyer.get(entity)

        val buildingLabel = TextButton("Building", defaultButtonStyle)
        buildingLabel.label.setFontScale(0.17f)

        val sellLabel = TextButton("Sell", defaultButtonStyle)
        sellLabel.label.setFontScale(0.17f)

        val workLabel = TextButton("Work", defaultButtonStyle)
        workLabel.label.setFontScale(0.17f)

        val behaviourLabel = TextButton("Beh", defaultButtonStyle)
        behaviourLabel.label.setFontScale(0.17f)

        val inventoryLabel = TextButton("Inv", defaultButtonStyle)
        inventoryLabel.label.setFontScale(0.17f)

        val buyingLabel = TextButton("Buying", defaultButtonStyle)
        buyingLabel.label.setFontScale(0.17f)

        val deleteLabel = TextButton("Delete", defaultButtonStyle)
        deleteLabel.label.setFontScale(0.17f)

        val exitLabel = TextButton("X", defaultButtonStyle)
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

        //Add the stuff to the main table
        this.mainTable.add(tabTable).expandX().fillX()
        this.mainTable.row()
        this.mainTable.add(contentTable).expand().fill()

        //Make the window
//        val background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE, 400, 600)))
        val windowBackground = NinePatchDrawable(NinePatch(MyGame.manager["dialog_box", Texture::class.java], 50, 50, 50, 50))
        val windowSkin = Window.WindowStyle(MyGame.manager["defaultFont", BitmapFont::class.java], Color.WHITE, windowBackground)

        //Window
        window = Window("", windowSkin)
        window.isMovable = true
        window.setSize(500f, 400f)
        window.setPosition(100f, 100f)
        window.pad(20f, 10f, 10f, 10f)

        window.add(this.mainTable).expand().fill()

        MyGame.stage.addActor(window)
    }

    override fun update(delta:Float){
        updateList.forEach { it() }
    }

    override fun close() {
        val debug = Mappers.debugDraw[currentlySelectedEntity]
        debug.debugDrawWorkers = false
        debug.debugDrawWorkplace = false

        mainTable.remove()
        currentlySelectedEntity = null
        window.remove()

        guiManager.closeWindow(this)
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
        scrollPaneStyle.vScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.BLACK)))
        scrollPaneStyle.hScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        scrollPaneStyle.hScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.BLACK)))

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

        topInfoTable.add(numWorkersLabel)

        mainTableArea.add(topInfoTable)
        mainTableArea.row()
        mainTableArea.add(mainTableWorkerInfo).fill().expand().pad(5f, 5f, 5f, 0f)
        mainTableArea.row()
        mainTableArea.add(bottomScrollPane)

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
        val populateScrollingTable = {
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
                        populateScrollingTable()
                        populateMainTableFunc(selectedWorkerEntity!!)
                    }
                }
            })
        }

        populateScrollingTable()

        table.add(leftScrollPane).width(100f).expandY().fillY()
        table.add(mainTableArea).expand().fill()

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

            comp.itemMap.values.forEach { item ->
                val itemLabel = Label(item.name, defaultLabelStyle)
                itemLabel.setFontScale(0.2f)
                itemLabel.setAlignment(Align.center)

                val itemAmountLabel = Label("${item.amount}", defaultLabelStyle)
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
        val taxRateTable = Table()
        taxRateTable.background = darkBackgroundDrawable

        val sellingItemsTable = Table()
        sellingItemsTable.background = darkBackgroundDrawable

        val sellHistoryTable = Table()
        sellHistoryTable.background = darkBackgroundDrawable

        //Set up the selling title
        val sellLabel = Label("Selling", defaultLabelStyle)
        sellLabel.setFontScale(0.25f)
        sellLabel.setAlignment(Align.center)

        //This will populate the table of items being sold
        val populateItemsTable = {
            sellingItemsTable.clear()

            sellingItemsTable.add(sellLabel).colspan(2)
            sellingItemsTable.row()

            comp.currSellingItems.forEach { (itemName, itemPrice) ->
                val itemNameLabel = Label(itemName, defaultLabelStyle)
                itemNameLabel.setFontScale(0.2f)
                itemNameLabel.setAlignment(Align.center)
                val itemAmountLabel = Label(itemPrice.toString(), defaultLabelStyle)
                itemAmountLabel.setFontScale(0.2f)
                itemAmountLabel.setAlignment(Align.center)

                sellingItemsTable.add(itemNameLabel).width(100f)
                sellingItemsTable.add(itemAmountLabel).width(100f)
                sellingItemsTable.row()
            }
        }

        populateItemsTable()

        //--- Tax rate section ---

        val taxRateLabel = Label("${comp.taxRate}", defaultLabelStyle)
        taxRateLabel.setFontScale(0.2f)
        taxRateLabel.setAlignment(Align.center)

        val lessTaxButton = TextButton("<", defaultButtonStyle)
        lessTaxButton.label.setFontScale(0.2f)

        val moreTaxButton = TextButton(">", defaultButtonStyle)
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
            val limit = Math.max(0, comp.sellHistory.size - 5)

            //For each history, set up the labels
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

        //Put the history function into our update map
        updateMap.put("sellHistory", historyTableFunc)

        updateList.add { populateItemsTable() }

        //Put in the event system
        EventSystem.onEvent("guiUpdateSellHistory", {historyTableFunc()})

        changedTabsFunc = { EventSystem.removeEvent("guiUpdateSellHistory")}

        table.top()

        //Add all the stuff to the table
        table.add(taxRateTable).expandX().fillX().padTop(20f) //The tax rate
        table.row().expandX().fillX().spaceTop(20f)
        table.add(sellingItemsTable).expandX().fillX() //What we are selling
        table.row().expandX().fillX().spaceTop(20f)
        table.add(sellHistoryTable).expandX().fillX() //The history table
        table.row().expand().fill() //Push everything up!
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

        table.add(linkButton) //The link button
        table.row().expandX().fillX()

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

                                    val list = Array<ItemPriceLink>()
                                    otherSelling.currSellingItems.forEach { (itemName, itemPrice) ->
                                        list.add(ItemPriceLink(itemName, (itemPrice * 1.5).toInt()))
                                    }

                                    //Add a EntityListLink to the reselling entity item links list
                                    comp.resellingEntityItemLinks.add(EntityListLink(ent, list))

                                    //Add the list of items to our selling list to let pawns know we are selling stuff
                                    list.forEach { itemLink ->
                                        //We need to make sure that the selling items doesn't already contain the item
                                        if(!selling.currSellingItems.any{it.itemName == itemLink.itemName})
                                            selling.currSellingItems.add(itemLink)
                                    }

                                    otherSelling.currSellingItems.clear()
                                }
                            }
                        }
                    }
                }
            }
        })
    }
}