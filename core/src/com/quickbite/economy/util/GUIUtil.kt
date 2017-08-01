package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.MyGame
import com.quickbite.economy.addChangeListener
import com.quickbite.economy.behaviour.Tasks
import com.quickbite.economy.components.WorkForceComponent
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ReloadGUIEvent
import com.quickbite.economy.gui.EntityWindowController
import com.quickbite.economy.gui.GameScreenGUIManager
import com.quickbite.economy.isValid
import com.quickbite.economy.objects.SelectedWorkerAndTable

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
            workerListTable.add(workerTable).growX()
            workerListTable.add() //Empty space for divider in titles
            workerListTable.row().growX()

            //Since this table gets updated on changes, make sure to keep the background if we reload this table
            //Also, we check the entity because the tables are new and won't match. We need to set the table again
            selectedWorkers.firstOrNull { it.worker == entity }?.run {
                workerTable.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.GRAY))) //Set the background of the selected table
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

    fun makeWorkerTable(entity:Entity, workforceComp:WorkForceComponent, labelStyle:Label.LabelStyle, textButtonStyle:TextButton.TextButtonStyle, openEntityWindowFunc:(Entity)->Unit):Table{
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
            openEntityWindowFunc(entity)
        }

        val workerTable = Table()

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

    fun makeSimpleLabelTooltip(message:String){
        val table = GameScreenGUIManager.toolTipTable
        table.clear()

        val messageLabel = Label(message, Label.LabelStyle(MyGame.defaultFont14, Color.WHITE))
        table.add(messageLabel)
    }

    fun makeEntityTooltip(entity:Entity){
        val table = GameScreenGUIManager.toolTipTable
        table.clear()

        val ic = Mappers.identity[entity]
        val pc = Mappers.produces[entity]
        val inv = Mappers.inventory[entity]
        val rc = Mappers.resource[entity]

        val titleMessage = Label(ic.name, Label.LabelStyle(MyGame.defaultFont20, Color.WHITE))

        table.add(titleMessage).colspan(100).left() //Add the title
        table.row() //Add a row, stuff goes under the title

        //For each component we want a table to hold its stuff

        //For the production...
        if(pc != null && pc.productionList.size > 0){
            val productionTable = Table()
            val producingTitleLabel = Label("Producing", GameScreenGUIManager.defaultLabelStyle)
            productionTable.add(producingTitleLabel)
            productionTable.row()
            pc.productionList.forEach {
                val production = Label(it.produceItemName, GameScreenGUIManager.defaultLabelStyle)
                productionTable.add(production).padLeft(5f)
                productionTable.row()
            }

            table.add(productionTable)
        }

        //For the inventory...
        if(inv != null){
            val invTable = Table()
            val invTitleLabel = Label("Inventory", GameScreenGUIManager.defaultLabelStyle)
            invTable.add(invTitleLabel)
            invTable.row()
            inv.itemMap.values.forEach {
                val production = Label("${it.itemAmount} ${it.itemName}", GameScreenGUIManager.defaultLabelStyle)
                invTable.add(production)
                invTable.row()
            }

            table.add(invTable)
        }

        //For the resource...
        if(rc != null){
            val resourceTable = Table()
            val resourceTitleLabel = Label("Resources", GameScreenGUIManager.defaultLabelStyle)
            resourceTable.add(resourceTitleLabel)
            resourceTable.row()
            val production = Label("${rc.currResourceAmount} ${rc.harvestItemName}", GameScreenGUIManager.defaultLabelStyle)
            resourceTable.add(production)

            table.add(resourceTable)
        }
    }
}