package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.CapitalizeEachToken
import com.quickbite.economy.MyGame
import com.quickbite.economy.addChangeListener
import com.quickbite.economy.behaviour.Tasks
import com.quickbite.economy.components.SellingItemsComponent
import com.quickbite.economy.components.WorkForceComponent
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ItemAmountChangeEvent
import com.quickbite.economy.event.events.ReloadGUIEvent
import com.quickbite.economy.gui.EntityWindowController
import com.quickbite.economy.gui.GameScreenGUIManager
import com.quickbite.economy.isValid
import com.quickbite.economy.objects.SelectedWorkerAndTable
import com.quickbite.economy.objects.SellingItemData

/**
 * Created by Paha on 5/20/2017.
 */
object GUIUtil {

    fun populateWorkerTable(workforceComp:WorkForceComponent, selectedWorkers:Array<SelectedWorkerAndTable>, workerListTable:Table, labelStyle:Label.LabelStyle,
                            textButtonStyle: TextButton.TextButtonStyle, guiManager:GameScreenGUIManager){

        workerListTable.clear()

        val nameTitleLabel = Label("Name", labelStyle)
        nameTitleLabel.setAlignment(Align.center)
        nameTitleLabel.setFontScale(1.2f)
        val currTaskTitleLabel = Label("Tasks", labelStyle)
        currTaskTitleLabel.setAlignment(Align.center)
        currTaskTitleLabel.setFontScale(1.2f)
        val startTimeTitleLAbel = Label("Start", labelStyle)
        startTimeTitleLAbel.setAlignment(Align.center)
        startTimeTitleLAbel.setFontScale(1.2f)
        val endTimeTitleLabel = Label("End", labelStyle)
        endTimeTitleLabel.setAlignment(Align.center)
        endTimeTitleLabel.setFontScale(1.2f)
        val salaryTitleLabel = Label("Salary", labelStyle)
        salaryTitleLabel.setAlignment(Align.center)
        salaryTitleLabel.setFontScale(1.2f)
        val fireTitleLabel = Label("Fire", labelStyle)
        fireTitleLabel.setAlignment(Align.center)
        fireTitleLabel.setFontScale(1.2f)
        val infoTitleLabel = Label("Info", labelStyle)
        infoTitleLabel.setAlignment(Align.center)
        infoTitleLabel.setFontScale(1.2f)

        //Make the title table...
        val titleTable = Table()

        //Add all the titles....
        titleTable.add(nameTitleLabel).growX().uniformX()
        titleTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        titleTable.add(currTaskTitleLabel).growX().uniformX()
        titleTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        titleTable.add(startTimeTitleLAbel).growX().uniformX()
        titleTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        titleTable.add(endTimeTitleLabel).growX().uniformX()
        titleTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        titleTable.add(salaryTitleLabel).growX().uniformX()
        titleTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        titleTable.add(infoTitleLabel).growX().uniformX()
        titleTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        titleTable.add(fireTitleLabel).growX().uniformX()

        //Add the title table and a row for the list of workers...
        workerListTable.add(titleTable).growX()
        workerListTable.row().growX()

        //For each worker, lets get some info and make a table for it!
        workforceComp.workersAvailable.forEach { entity ->
            if(!entity.isValid()) return@forEach //If it's not valid, continue...

            val workerTable = GUIUtil.makeWorkerTable(entity, workforceComp, labelStyle, textButtonStyle, {guiManager.openEntityWindow(it)})

            //Add the worker table
            workerListTable.add(workerTable).growX().spaceBottom(3f)
            workerListTable.add() //Empty space for divider in titles
            workerListTable.row().growX()

            //Since this table gets updated on changes, make sure to keep the background if we reload this table
            //Also, we check the entity because the tables are new and won't match. We need to set the table again
            selectedWorkers.firstOrNull { it.worker == entity }?.run {
                workerTable.background = NinePatchDrawable(NinePatch(MyGame.manager["dialog_box_thin_selected", Texture::class.java], 3, 3, 3, 3)) //Set the background of the selected table
                this.table = workerTable
            }

            workerTable.addListener(object:ClickListener(){
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    super.touchUp(event, x, y, pointer, button)

                    EntityWindowController.changeWorkerSelectionInTable(selectedWorkers, entity, workerTable)
                }
            })
        }
    }

    fun populateWorkerTable(workers:List<WorkerCompLink>, selectedWorkers:Array<SelectedWorkerAndTable>, workerListTable:Table, labelStyle:Label.LabelStyle,
                            textButtonStyle: TextButton.TextButtonStyle, guiManager:GameScreenGUIManager){

        workerListTable.clear()

        val nameTitleLabel = Label("Name", labelStyle)
        nameTitleLabel.setAlignment(Align.center)
        nameTitleLabel.setFontScale(1.2f)
        val currTaskTitleLabel = Label("Tasks", labelStyle)
        currTaskTitleLabel.setAlignment(Align.center)
        currTaskTitleLabel.setFontScale(1.2f)
        val startTimeTitleLAbel = Label("Start", labelStyle)
        startTimeTitleLAbel.setAlignment(Align.center)
        startTimeTitleLAbel.setFontScale(1.2f)
        val endTimeTitleLabel = Label("End", labelStyle)
        endTimeTitleLabel.setAlignment(Align.center)
        endTimeTitleLabel.setFontScale(1.2f)
        val salaryTitleLabel = Label("Salary", labelStyle)
        salaryTitleLabel.setAlignment(Align.center)
        salaryTitleLabel.setFontScale(1.2f)
        val fireTitleLabel = Label("Fire", labelStyle)
        fireTitleLabel.setAlignment(Align.center)
        fireTitleLabel.setFontScale(1.2f)
        val infoTitleLabel = Label("Info", labelStyle)
        infoTitleLabel.setAlignment(Align.center)
        infoTitleLabel.setFontScale(1.2f)

        //Make the title table...
        val titleTable = Table()

        //Add all the titles....
        titleTable.add(nameTitleLabel).growX().uniformX()
        titleTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        titleTable.add(currTaskTitleLabel).growX().uniformX()
        titleTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        titleTable.add(startTimeTitleLAbel).growX().uniformX()
        titleTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        titleTable.add(endTimeTitleLabel).growX().uniformX()
        titleTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        titleTable.add(salaryTitleLabel).growX().uniformX()
        titleTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        titleTable.add(infoTitleLabel).growX().uniformX()
        titleTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        titleTable.add(fireTitleLabel).growX().uniformX()

        //Add the title table and a row for the list of workers...
        workerListTable.add(titleTable).growX()
        workerListTable.row().growX()

        //For each worker, lets get some info and make a table for it!
        workers.forEach { (entity, comp) ->
            if(!entity.isValid()) return@forEach //If it's not valid, continue...

            val workerTable = GUIUtil.makeWorkerTable(entity, comp, labelStyle, textButtonStyle, {guiManager.openEntityWindow(it)})

            //Add the worker table
            workerListTable.add(workerTable).growX().spaceBottom(3f)
            workerListTable.add() //Empty space for divider in titles
            workerListTable.row().growX()

            //Since this table gets updated on changes, make sure to keep the background if we reload this table
            //Also, we check the entity because the tables are new and won't match. We need to set the table again
            //TLDR: Resets the background for the selected worker that *may* have been updated
            selectedWorkers.firstOrNull { it.worker == entity }?.run {
                workerTable.background = NinePatchDrawable(NinePatch(MyGame.manager["dialog_box_thin_selected", Texture::class.java], 3, 3, 3, 3)) //Set the background of the selected table
                this.table = workerTable
            }

            workerTable.addListener(object:ClickListener(){
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    super.touchUp(event, x, y, pointer, button)

                    EntityWindowController.changeWorkerSelectionInTable(selectedWorkers, entity, workerTable)
                }
            })
        }
    }

    /**
     * Creates the individual worker table to be used in another table
     * @param entity The entity to use for information
     * @param workforceComp The WorkForceComponent that the worker (entity) is tied to
     * @param labelStyle The label style to use for the labels
     * @param textButtonStyle The button style to use for buttons (fire button)
     * @param entityInfoFunc The callback to execute when the information button is clicked
     */
    fun makeWorkerTable(entity:Entity, workforceComp:WorkForceComponent, labelStyle:Label.LabelStyle, textButtonStyle:TextButton.TextButtonStyle, entityInfoFunc:(Entity)->Unit):Table{
        val worker = Mappers.worker[entity]
        val id = Mappers.identity[entity]

        var tasks = ""
        worker.taskList.forEachIndexed { index, task -> tasks += "${task[0].toUpperCase()}${if (index < worker.taskList.size - 1) "," else ""} " } //The complicated bit at the end controls the ending comma

        //The name and task label
        val workerNameLabel = Label(id.name, labelStyle)
        workerNameLabel.setAlignment(Align.center)
        val workerTasksLabel = Label(tasks, labelStyle)
        workerTasksLabel.setAlignment(Align.center)

        //The start and end moveTime, with controls to handle each (slightly complicated)
        val startTimeTable = Table()
        val endTimeTable = Table()
        val workHoursStartLabel = Label("${worker.timeRange.first}", labelStyle)
        workHoursStartLabel.setAlignment(Align.center)
        val workHoursEndLabel = Label("${worker.timeRange.second}", labelStyle)
        workHoursEndLabel.setAlignment(Align.center)

        val workHourStartLessButton = TextButton("-", textButtonStyle)
        val workHourStartMoreButton = TextButton("+", textButtonStyle)
        val workHourEndLessButton = TextButton("-", textButtonStyle)
        val workHourEndMoreButton = TextButton("+", textButtonStyle)

        workHourStartLessButton.addChangeListener { _, _ ->
            worker.timeRange.first = Math.floorMod(worker.timeRange.first - 1, 24)
            workHoursStartLabel.setText("${worker.timeRange.first}")
        }

        workHourStartMoreButton.addChangeListener { _, _ ->
            worker.timeRange.first = Math.floorMod(worker.timeRange.first + 1, 24)
            workHoursStartLabel.setText("${worker.timeRange.first}")
        }

        workHourEndLessButton.addChangeListener { _, _ ->
            worker.timeRange.second = Math.floorMod(worker.timeRange.second - 1, 24)
            workHoursEndLabel.setText("${worker.timeRange.second}")
        }

        workHourEndMoreButton.addChangeListener { _, _ ->
            worker.timeRange.second = Math.floorMod(worker.timeRange.second + 1, 24)
            workHoursEndLabel.setText("${worker.timeRange.second}")
        }

        val salaryLabel = Label("${worker.dailyWage}", labelStyle)
        salaryLabel.setAlignment(Align.center)

        startTimeTable.add(workHourStartLessButton).size(16f)
        startTimeTable.add(workHoursStartLabel).space(0f, 5f, 0f, 5f).width(20f)
        startTimeTable.add(workHourStartMoreButton).size(16f)

        endTimeTable.add(workHourEndLessButton).size(16f)
        endTimeTable.add(workHoursEndLabel).space(0f, 5f, 0f, 5f).width(20f)
        endTimeTable.add(workHourEndMoreButton).size(16f)

        //This is for removing the entity from the work building
        val removeWorkerButton = TextButton("x", textButtonStyle)
        removeWorkerButton.addListener(object: ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                workforceComp.workersAvailable.removeValue(entity, true) //Remove the entity
                if(entity.isValid()) {
                    val bc = Mappers.behaviour[entity]
                    bc.currTask = Tasks.leaveMap(bc.blackBoard) //Make the entity leave the map and be destroyed
                }
                GameEventSystem.fire(ReloadGUIEvent())
            }
        })

        val infoButton = TextButton("?", textButtonStyle)
        infoButton.addChangeListener { _, _ ->
            entityInfoFunc(entity)
        }

        val workerTable = Table()
        workerTable.background = NinePatchDrawable(NinePatch(MyGame.manager["dialog_box_thin", Texture::class.java], 3, 3, 3, 3))

        workerTable.add(workerNameLabel).growX().uniformX()
        workerTable.add(workerTasksLabel).growX().uniformX()
        workerTable.add(startTimeTable).growX().uniformX()
        workerTable.add(endTimeTable).growX().uniformX()
        workerTable.add(salaryLabel).growX().uniformX()
        workerTable.add(infoButton).growX().uniformX().size(16f)
        workerTable.add(removeWorkerButton).growX().uniformX().size(16f)

        return workerTable
    }

    fun populateWorkerTasksAndAmountsTable(workforceComp:WorkForceComponent, workerTasksAndAmountsTable:Table, labelStyle: Label.LabelStyle):Table{
        workerTasksAndAmountsTable.clear()
        val currWorkersAndTotalWorkers = Label("workers: ${workforceComp.workersAvailable.size}/${workforceComp.numWorkerSpots}", labelStyle)

        workerTasksAndAmountsTable.add(currWorkersAndTotalWorkers).colspan(100)
        workerTasksAndAmountsTable.row()

        val size = workforceComp.workerTasksLimits.size
        for(i in 0..size-1){
            val workerTasksLimit = workforceComp.workerTasksLimits[i]
            //The current amount out of the max amount, ie: 1/4
            val amountText = "${workforceComp.workerTaskMap[workerTasksLimit.taskName]!!.size}/${workforceComp.workerTasksLimits.find { it.taskName == workerTasksLimit.taskName }!!.amount}"

            val taskLabel = Label(workerTasksLimit.taskName, labelStyle)
            val amountLabel = Label(amountText, labelStyle)

            workerTasksAndAmountsTable.add(taskLabel).spaceRight(5f)
            workerTasksAndAmountsTable.add(amountLabel).width(25f).spaceRight(5f)

            if(i < workforceComp.workerTasksLimits.size - 1) {
                val dashLabel = Label(" - ", labelStyle)
                workerTasksAndAmountsTable.add(dashLabel).space(0f, 5f, 0f, 5f)
            }
        }

        return workerTasksAndAmountsTable
    }

    /**
     * Makes a simple label tooltip for things like item names when hovering over icons
     * @param message The message to put in the tooltip
     */
    fun makeSimpleLabelTooltip(message:String){
        val table = GameScreenGUIManager.toolTipTable
        table.clear()

        val messageLabel = Label(message, Label.LabelStyle(MyGame.defaultFont14, Color.WHITE))
        table.add(messageLabel).pad(2f)
    }

    /**
     * Makes a more complicated tooltip for a quickview of an Entity's essentials
     * @param entity The Entity to get info from
     */
    fun makeEntityTooltip(entity:Entity){
        val table = GameScreenGUIManager.toolTipTable
        GameScreenGUIManager.showingTooltip.entity = entity

        val ic = Mappers.identity[entity]
        val pc = Mappers.produces[entity]
        val inv = Mappers.inventory[entity]
        val rc = Mappers.resource[entity]

        fun createTooltip(){
            table.clear()

            val titleMessage = Label(ic.name, Label.LabelStyle(MyGame.defaultFont20, Color.WHITE))

            table.add(titleMessage).colspan(100).left() //Add the title
            table.row() //Add a row, stuff goes under the title

            //For each component we want a table to hold its stuff

            //For the production...
            if (pc != null && pc.productionList.size > 0) {
                val productionTable = Table()
                val producingTitleLabel = Label("Producing", GameScreenGUIManager.defaultLabelStyle)
                productionTable.add(producingTitleLabel)
                productionTable.row()
                pc.productionList.forEach {
                    val production = Label(it.produceItemName, GameScreenGUIManager.defaultLabelStyle)
                    productionTable.add(production).padLeft(5f)
                    productionTable.row()
                }

                table.add(productionTable).spaceRight(5f).top()
            }

            //For the inventory...
            if (inv != null) {
                val invTable = Table()
                val invTitleLabel = Label("Inventory", GameScreenGUIManager.defaultLabelStyle)
                invTable.add(invTitleLabel)
                invTable.row()
                inv.itemMap.values.forEach {
                    val itemLabel = Label("${it.itemAmount} ${it.itemName}", GameScreenGUIManager.defaultLabelStyle)
                    itemLabel.setAlignment(Align.left)
                    invTable.add(itemLabel).fillX()
                    invTable.row()
                }
                table.add(invTable).spaceRight(5f).top()
            }

            //For the resource...
            if (rc != null) {
                val resourceTable = Table()
                val resourceTitleLabel = Label("Resources", GameScreenGUIManager.defaultLabelStyle)
                resourceTable.add(resourceTitleLabel)
                resourceTable.row()
                val resource = Label("${rc.currResourceAmount} ${rc.harvestItemName}", GameScreenGUIManager.defaultLabelStyle)
                resourceTable.add(resource)
                table.add(resourceTable).top()
            }
        }

        createTooltip()
        GameScreenGUIManager.showingTooltip.event = GameEventSystem.subscribe<ItemAmountChangeEvent>({createTooltip()}, ic.uniqueID)
    }

    fun populateItemsTable(comp:SellingItemsComponent, sellItemsMainTable:Table, defaultLabelStyle: Label.LabelStyle, defaultTextButtonStyle:TextButton.TextButtonStyle){
        sellItemsMainTable.clear()

        val sellItemsListTable = Table()

        val itemNameColTitle = Label("Name", defaultLabelStyle)
        itemNameColTitle.setAlignment(Align.center)

        val itemAmountColTitle = Label("Price", defaultLabelStyle)
        itemAmountColTitle.setAlignment(Align.center)

        val itemStockColTitle = Label("Stock", defaultLabelStyle)
        itemStockColTitle.setAlignment(Align.center)

        //Add the three titles
        sellItemsListTable.add().growX()
        sellItemsListTable.add(itemNameColTitle).width(100f)
        sellItemsListTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10))))
        sellItemsListTable.add(itemAmountColTitle).width(100f)
        sellItemsListTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10))))
        sellItemsListTable.add(itemStockColTitle).width(100f)
        sellItemsListTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10))))
        sellItemsListTable.add().width(16f).spaceLeft(10f) //Empty spot for the X button
        sellItemsListTable.add().growX()
        sellItemsListTable.row()

        comp.currSellingItems.forEach { sellItemData ->
            //The item name
            val itemNameLabel = Label(sellItemData.itemName.CapitalizeEachToken(), defaultLabelStyle)
            itemNameLabel.setAlignment(Align.center)

            //The item amount
            val itemAmountLabel = Label(sellItemData.itemPrice.toString(), defaultLabelStyle)
            itemAmountLabel.setAlignment(Align.center)

            /** The item stock amount */
            val itemStockTable = Table()

            val lessStockButton = TextButton("-", defaultTextButtonStyle)

            val moreStockButton = TextButton("+", defaultTextButtonStyle)

            fun getItemStockText():String =
                    if(sellItemData.itemStockAmount < 0) "max" else sellItemData.itemStockAmount.toString()

            val itemStockLabel = Label(getItemStockText(), defaultLabelStyle)
            itemStockLabel.setFontScale(1f)
            itemStockLabel.setAlignment(Align.center)

            itemStockTable.add(lessStockButton).size(16f)
            itemStockTable.add(itemStockLabel).space(0f, 5f, 0f, 5f).width(25f)
            itemStockTable.add(moreStockButton).size(16f)

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

            val itemTable = Table()
            itemTable.background = NinePatchDrawable(NinePatch(MyGame.manager["dialog_box_thin", Texture::class.java], 3, 3, 3, 3))

            //The x Label if we want to delete the link from a store that is reselling
            val xLabel = TextButton("X", defaultTextButtonStyle)
            xLabel.label.setAlignment(Align.center)

            itemTable.add().growX()
            itemTable.add(itemNameLabel).width(100f)
            itemTable.add().width(2f) //Empty space for the divider in the titles
            itemTable.add(itemAmountLabel).width(100f)
            itemTable.add().width(2f) //Empty space for the divider in the titles
            itemTable.add(itemStockTable).width(100f)
            itemTable.add().width(2f) //Empty space for the divider in the titles
            if(sellItemData.itemSourceType != SellingItemData.ItemSource.None)
                itemTable.add(xLabel).size(16f).spaceLeft(10f).right() //Either add the x label
            else
                itemTable.add().width(16f) //Or add an empty column
            itemTable.add().growX()

            sellItemsListTable.add(itemTable).colspan(100).growX()
            sellItemsListTable.row().spaceTop(5f)

            //The listener for the X button
            xLabel.addListener(object:ClickListener(){
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    super.touchUp(event, x, y, pointer, button)
                    Util.removeSellingItemFromReseller(comp, sellItemData.itemName, sellItemData.itemSourceType, sellItemData.itemSourceData)
                }
            })
        }

//        sellItemsListTable.debugAll()
        sellItemsMainTable.add(sellItemsListTable).growX()
    }

    fun populateHistoryTable(comp:SellingItemsComponent, sellHistoryTable:Table, defaultLabelStyle:Label.LabelStyle, maxWidth:Float){
        sellHistoryTable.clear()
        sellHistoryTable.top()

        val itemNameLabel = Label("Item", defaultLabelStyle)
        itemNameLabel.setAlignment(Align.center)

        val itemAmountLabel = Label("Amt", defaultLabelStyle)
        itemAmountLabel.setAlignment(Align.center)

        val pricePerUnitLabel = Label("PPU", defaultLabelStyle)
        pricePerUnitLabel.setAlignment(Align.center)

        val timeStampLabel = Label("Time", defaultLabelStyle)
        timeStampLabel.setAlignment(Align.center)

        val buyerNameLabel = Label("Buyer", defaultLabelStyle)
        buyerNameLabel.setAlignment(Align.center)

        val width = (maxWidth - maxWidth*0.05f - 4*2f)/5f //the 4*2f is for 4 line dividers at 2 pixels wide. The /5f is for 5 labels

        //Add all the titles
        sellHistoryTable.add(itemNameLabel).width(width)
        sellHistoryTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        sellHistoryTable.add(itemAmountLabel).width(width)
        sellHistoryTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        sellHistoryTable.add(pricePerUnitLabel).width(width)
        sellHistoryTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        sellHistoryTable.add(timeStampLabel).width(width)
        sellHistoryTable.add(Image(TextureRegion(Util.createPixel(Color(0.8f, 0.8f, 0.8f, 0.5f), 2, 10)))).uniformY().width(2f)
        sellHistoryTable.add(buyerNameLabel).width(width)
        sellHistoryTable.row()

        //TODO Don't use magic number to limit this size?
        //The limit of the history
        val limit = Math.max(0, comp.sellHistory.queue.size - 5)

        //For each history, set up the labels
        for (i in (comp.sellHistory.queue.size-1).downTo(limit)){
            val sell = comp.sellHistory.queue[i]
            val entryTable = Table()
            entryTable.background = NinePatchDrawable(NinePatch(MyGame.manager["dialog_box_thin", Texture::class.java], 3, 3, 3, 3))

            val _item = Label(sell.itemName, defaultLabelStyle)
            _item.setAlignment(Align.center)

            val _amount = Label(sell.itemAmount.toString(), defaultLabelStyle)
            _amount.setAlignment(Align.center)

            val _ppu = Label(sell.pricePerItem.toString(), defaultLabelStyle)
            _ppu.setAlignment(Align.center)

            val _time = Label(sell.timeStamp.toString(), defaultLabelStyle)
            _time.setAlignment(Align.center)

            val _buyer = Label(sell.buyerName, defaultLabelStyle)
            _buyer.setAlignment(Align.center)

            //Add them all to the table and add a new row
            entryTable.add(_item).width(width)
            entryTable.add().width(2f) //Empty space for divider in titles
            entryTable.add(_amount).width(width)
            entryTable.add().width(2f) //Empty space for divider in titles
            entryTable.add(_ppu).width(width)
            entryTable.add().width(2f) //Empty space for divider in titles
            entryTable.add(_time).width(width)
            entryTable.add().width(2f) //Empty space for divider in titles
            entryTable.add(_buyer).width(width)

            sellHistoryTable.add(entryTable).colspan(100).spaceTop(3f)
            sellHistoryTable.row()
        }

//        sellHistoryTable.debugAll()
    }
}