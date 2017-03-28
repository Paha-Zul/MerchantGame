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

    val defaultLabelStyle = Label.LabelStyle(MyGame.manager["defaultFont", BitmapFont::class.java], Color.BLACK)
    val mainTable = Table()

    val defaultButtonStyle = TextButton.TextButtonStyle()

    init {
        defaultButtonStyle.up = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.CYAN))) as Drawable?
        defaultButtonStyle.font = MyGame.manager["defaultFont", BitmapFont::class.java]
        defaultButtonStyle.fontColor = Color.WHITE

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
        val buying = Mappers.buyer.get(entity)

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

        val buyingLabel = Label("Buying", labelStyle)
        buyingLabel.setFontScale(0.2f)

        val deleteLabel = Label("Delete", labelStyle)
        deleteLabel.setFontScale(0.2f)

        val exitLabel = Label("X", labelStyle)
        exitLabel.setFontScale(0.2f)

        if(bc != null)
            tabTable.add(buildingLabel).spaceRight(10f)
        if(sc != null)
            tabTable.add(sellLabel).spaceRight(10f)
        if(wc != null)
            tabTable.add(workLabel).spaceRight(10f)
        if(behComp != null)
            tabTable.add(behaviourLabel).spaceRight(10f)
        if(ic != null)
            tabTable.add(inventoryLabel).spaceRight(10f)
        if(buying != null)
            tabTable.add(buyingLabel).spaceRight(10f)

        tabTable.add(deleteLabel).spaceRight(10f)

        tabTable.add().expandX().fillX()
        tabTable.add(exitLabel).right()

        buildingLabel.addListener(object: ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(bottomTable, bc)
                return true
            }
        })

        sellLabel.addListener(object: ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(bottomTable, sc)
                return true
            }
        })

        workLabel.addListener(object: ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(bottomTable, wc)
                return true
            }
        })

        behaviourLabel.addListener(object: ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(bottomTable, behComp)
                return true
            }
        })

        inventoryLabel.addListener(object: ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(bottomTable, ic)
                return true
            }
        })

        buyingLabel.addListener(object: ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadTable(bottomTable, buying)
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
            loadTable(bottomTable, currentlyDisplayingComponent!!)

        //Add the stuff to the main table
        mainTable.add(tabTable).expandX().fillX()
        mainTable.row()
        mainTable.add(bottomTable).expand().fill()

        //Make the window
        val background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE, 400, 600)))
        val windowSkin = Window.WindowStyle(MyGame.manager["defaultFont", BitmapFont::class.java], Color.BLACK, background)

        //Window
        window = Window("", windowSkin)
        window.isMovable = true
        window.setSize(500f, 400f)
        window.setPosition(100f, 100f)
        window.padTop(20f)

        window.add(mainTable).expandX().fillX()
        window.debugAll()

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
        scrollPaneStyle.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        scrollPaneStyle.vScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        scrollPaneStyle.vScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.BLACK)))
        scrollPaneStyle.hScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        scrollPaneStyle.hScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.BLACK)))

        val buttonStyle = Button.ButtonStyle()

        //Our scroll pane
        val workerList = Table() //Our worker list

        val leftScrollPane = ScrollPane(workerList, scrollPaneStyle)

        //The main table area.
        val mainTableArea = Table()
        val workerTaskList = Table()
        val bottomScrollPane = ScrollPane(workerTaskList, scrollPaneStyle)

        val mainTableWorkerInfo = Table()

        mainTableArea.add(mainTableWorkerInfo).fill().expand()
        mainTableArea.row()
        mainTableArea.add(bottomScrollPane)

        var selectedWorkerTaskLink: WorkerTaskData? = null

        //The function to populate the main table where the worker and info are displayed
        val populateMainTableFunc = { workerTaskLink: WorkerTaskData ->
            mainTableWorkerInfo.clear()

            val identity = Mappers.identity[workerTaskLink.entity]

            val mainNameLabel = Label(identity.name, defaultLabelStyle)
            mainNameLabel.setFontScale(0.4f)

            val mainTasksLabel = Label(workerTaskLink.taskList.joinToString(), defaultLabelStyle)
            mainTasksLabel.setFontScale(0.25f)

            mainTableWorkerInfo.add(mainNameLabel)
            mainTableWorkerInfo.row()
            mainTableWorkerInfo.add(mainTasksLabel)

            selectedWorkerTaskLink = workerTaskLink
        }

        //The function to populate the left side scrolling table where the list of workers are displayed
        val populateScrollingTable = {
            workerList.clear() //Clear the worker list from any leftover junk

            //Populate the left scrolling table
            comp.workersAvailable.forEach { workerTaskLink ->
                val identity = Mappers.identity[workerTaskLink.entity]

                val workerButton = Button(buttonStyle)

                val nameLabel = Label(identity.name, defaultLabelStyle)
                nameLabel.setFontScale(0.25f)

                var tasks = ""
                workerTaskLink.taskList.forEach { task -> tasks += "${task[0].toUpperCase()}, " }

                val tasksLabel = Label(tasks, defaultLabelStyle)
                tasksLabel.setFontScale(0.18f)

                //Add the name and tasks to our worker table button
                workerButton.add(nameLabel)
                workerButton.row()
                workerButton.add(tasksLabel)

                workerButton.setSize(100f, 100f)

                workerButton.debugAll()

                //Add the worker table and a row (we want it to be a vertical list)
                workerList.add(workerButton)
                workerList.row()

                //When we click a worker, let's populate the main area with info
                workerButton.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        super.clicked(event, x, y)

                        populateMainTableFunc(workerTaskLink)
                    }
                })
            }
        }

        //For the tasks available, list them
        comp.workerTasks.forEach { taskName ->
            val taskNameLabel = Label(taskName, defaultLabelStyle)
            taskNameLabel.setFontScale(0.2f)

            workerTaskList.add(taskNameLabel).space(0f, 5f, 0f, 5f)

            //When we click the task name label: if it's not owned by the worker, remove it. Otherwise, add it
            taskNameLabel.addListener(object:ClickListener(){
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    super.clicked(event, x, y)

                    //If the link is not null, lets do stuff
                    if(selectedWorkerTaskLink != null){
                        val taskNameText = taskNameLabel.text.toString()

                        //If it doesn't contain it, add it. Otherwise, remove it
                        if(!selectedWorkerTaskLink!!.taskList.contains(taskNameText))
                            selectedWorkerTaskLink!!.taskList.add(taskNameText)
                        else
                            selectedWorkerTaskLink!!.taskList.removeValue(taskNameText, false)

                        //We need to update the main table and worker list
                        populateScrollingTable()
                        populateMainTableFunc(selectedWorkerTaskLink!!)
                    }
                }
            })
        }

        populateScrollingTable()

        table.add(leftScrollPane).maxWidth(100f)
        table.add(mainTableArea).expand().fill()

        table.debugAll()

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
        val amountLabel = Label("Amount of items: ${comp.itemMap.size}", defaultLabelStyle)
        amountLabel.setFontScale(0.2f)
        val listLabel = Label("Item List: ${comp.itemMap.values}", defaultLabelStyle)
        listLabel.setFontScale(0.2f)
        listLabel.setWrap(true)

        table.add(amountLabel).expandX().fillX()
        table.row()
        table.add(listLabel).expandX().fillX()

        updateList.add({amountLabel.setText("Amount of items: ${comp.itemMap.size}")})
        updateList.add({listLabel.setText("Item List: ${comp.itemMap.values}")})
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
        val sellHistoryTable = Table()
        val taxRateTable = Table()

        val sellLabel = Label("Selling: ${comp.currSellingItems}", defaultLabelStyle)
        sellLabel.setFontScale(0.2f)
        sellLabel.setWrap(true)

        val taxRateLabel = Label("${comp.taxRate}", defaultLabelStyle)
        taxRateLabel.setFontScale(0.2f)
        taxRateLabel.setAlignment(Align.center)

        val lessTaxButton = TextButton("<", defaultButtonStyle)
        lessTaxButton.label.setFontScale(0.2f)

        val moreTaxButton = TextButton(">", defaultButtonStyle)
        moreTaxButton.label.setFontScale(0.2f)

        taxRateTable.add(lessTaxButton)
        taxRateTable.add(taxRateLabel).width(50f)
        taxRateTable.add(moreTaxButton)

        taxRateTable.debugAll()

        //If we are also reselling, add this stuff
        if(comp.isReselling)
            setupResellingStuff(table, comp)

        table.add(taxRateTable).expandX().fillX() //What we are selling
        table.row().expandX().fillX()
        table.add(sellLabel).expandX().fillX() //What we are selling
        table.row().expandX().fillX()
        table.add(sellHistoryTable).expandX().fillX() //The history table

        //The titles for all the columns
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

        //Put in the event system
        EventSystem.onEvent("guiUpdateSellHistory", {historyTableFunc()})

        //Add the selling label to the update list
        updateList.add({sellLabel.setText("Selling: ${comp.currSellingItems}")})

        changedTabsFunc = { EventSystem.removeEvent("guiUpdateSellHistory")}
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