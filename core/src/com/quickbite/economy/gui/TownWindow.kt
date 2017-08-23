package com.quickbite.economy.gui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.MyGame
import com.quickbite.economy.addChangeListener
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.PopulationChangeEvent
import com.quickbite.economy.event.events.ReloadGUIEvent
import com.quickbite.economy.gui.widgets.Graph
import com.quickbite.economy.managers.TownManager
import com.quickbite.economy.objects.SelectedWorkerAndTable
import com.quickbite.economy.util.*

/**
 * Created by Paha on 4/9/2017.
 */
class TownWindow : GUIWindow() {
    lateinit var updatePopGraphEvent:GameEventSystem.GameEventRegistration
    private val town by lazy { TownManager.getTown("Town") }
    private var selectedWorkers = Array<SelectedWorkerAndTable>()

    init{
        window.titleLabel.setText("Town")
        initTabs()
        changeTabs("pop")
    }

    private fun initTabs(){
        val popButton = TextButton("Pop", defaultTextButtonStyle).apply { name = "pop" }
        val workersButton = TextButton("Workers", defaultTextButtonStyle).apply { name = "workers" }
        val incomeButton = TextButton("Income", defaultTextButtonStyle).apply { name = "incomes" }

        popButton.addChangeListener { _, _ -> changeTabs("pop") }
        workersButton.addChangeListener { _, _ -> changeTabs("workers") }
        incomeButton.addChangeListener { _, _ -> changeTabs("income") }

        tabTable.add(popButton).right().size(75f, TAB_HEIGHT).spaceRight(5f)
        tabTable.add(workersButton).right().size(75f, TAB_HEIGHT).spaceRight(5f)
        tabTable.add(incomeButton).right().size(75f, TAB_HEIGHT).spaceRight(5f)
        tabTable.add().expandX().fillX()
    }

    private fun changeTabs(type:String){
        changedTabsFunc()
        changedTabsFunc = {}

        updateFuncsList.clear()
        contentTable.clear()

        when(type){
            "pop" -> setupPopTab()
            "workers" -> setupWorkersTable()
            "income" -> createIncomeTable()
        }
    }

    private fun setupPopTab(){
        val style = Graph.GraphStyle(TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE))), Color.WHITE, MyGame.defaultFont14)
        style.graphBackground = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.DARK_GRAY)))
        style.lineThickness = 2f

        val popTitleLabel = Label("Population:", defaultLabelStyle)
        val popAmtLabel = Label("${town.population.toInt()}", defaultLabelStyle)

        //TODO These should probably become progress bars
        val needsTitleLabel = Label("Needs:", defaultLabelStyle)
        val needsAmtLabel = Label("${town.needsRating}", defaultLabelStyle)

        val luxuryTitleLabel = Label("Luxury:", defaultLabelStyle)
        val luxuryAmtLabel = Label("${town.luxuryRating}", defaultLabelStyle)

        val graph = Graph(TownManager.getTown("Town").populationHistory.queue.toList(), 50, style)

        updatePopGraphEvent = GameEventSystem.subscribe<PopulationChangeEvent> {
            graph.points = it.popHistory
        }

        updateFuncsList.add {
            popAmtLabel.setText("${town.population}")
            needsAmtLabel.setText("${town.needsRating}")
            luxuryAmtLabel.setText("${town.luxuryRating}")
        }

        changedTabsFunc = { GameEventSystem.unsubscribe(updatePopGraphEvent) }

        contentTable.add(needsTitleLabel).uniformX()
        contentTable.add(needsAmtLabel).uniformX()
        contentTable.add().growX()
        contentTable.row()
        contentTable.add(luxuryTitleLabel).uniformX()
        contentTable.add(luxuryAmtLabel).uniformX()
        contentTable.add().growX()
        contentTable.row()
        contentTable.add(popTitleLabel).uniformX()
        contentTable.add(popAmtLabel).uniformX()
        contentTable.add().growX()
        contentTable.row()
        contentTable.add(graph).size(300f, 200f).colspan(3)

        contentTable.setFillParent(true)
        contentTable.top().left()
    }

    private fun setupWorkersTable(){
        //TODO This doesn't update if it's open and worker building is destroyed, hmm....

        val scrollPaneStyle = ScrollPane.ScrollPaneStyle()
        scrollPaneStyle.background = darkBackgroundDrawable
        scrollPaneStyle.vScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        scrollPaneStyle.vScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color(Color.BLACK))))
        scrollPaneStyle.hScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        scrollPaneStyle.hScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color(Color.BLACK))))

        val buttonStyle = Button.ButtonStyle()
        buttonStyle.up = buttonBackgroundDrawable

        //Our scroll pane
        val workerListTable = Table() //Our worker list
        val mainScrollPane = ScrollPane(workerListTable, scrollPaneStyle)

        workerListTable.top()

        fun populateWorkerTable() {
            workerListTable.clear()

            val nameTitleLabel = Label("Name", defaultLabelStyle)
            nameTitleLabel.setAlignment(Align.center)
            nameTitleLabel.setFontScale(1.2f)
            val currTaskTitleLabel = Label("Tasks", defaultLabelStyle)
            currTaskTitleLabel.setAlignment(Align.center)
            currTaskTitleLabel.setFontScale(1.2f)
            val startTimeTitleLAbel = Label("Start", defaultLabelStyle)
            startTimeTitleLAbel.setAlignment(Align.center)
            startTimeTitleLAbel.setFontScale(1.2f)
            val endTimeTitleLabel = Label("End", defaultLabelStyle)
            endTimeTitleLabel.setAlignment(Align.center)
            endTimeTitleLabel.setFontScale(1.2f)
            val salaryTitleLabel = Label("Salary", defaultLabelStyle)
            salaryTitleLabel.setAlignment(Align.center)
            salaryTitleLabel.setFontScale(1.2f)
            val fireTitleLabel = Label("Fire", defaultLabelStyle)
            fireTitleLabel.setAlignment(Align.center)
            fireTitleLabel.setFontScale(1.2f)
            val infoTableLabel = Label("Info", defaultLabelStyle)
            infoTableLabel.setAlignment(Align.center)
            infoTableLabel.setFontScale(1.2f)

            val titleTable = Table()
            workerListTable.add(titleTable).growX()
            workerListTable.row().growX()

            //Add all the titles....
            titleTable.add(nameTitleLabel).growX().uniformX()
            titleTable.add(currTaskTitleLabel).growX().uniformX()
            titleTable.add(startTimeTitleLAbel).growX().uniformX()
            titleTable.add(endTimeTitleLabel).growX().uniformX()
            titleTable.add(fireTitleLabel).growX().uniformX()
            titleTable.add(salaryTitleLabel).growX().uniformX()

            val workerCompList = mutableListOf<WorkerCompLink>()

            Families.workBuildings.forEach { workBuildingEnt ->
                val workForceComp = Mappers.workforce[workBuildingEnt]

                workForceComp.workersAvailable.forEach workers@{ entity ->
                    workerCompList += WorkerCompLink(entity, workForceComp)
//                    if(!entity.isValid()) return@workers
//
//                    val workerTable = GUIUtil.makeWorkerTable(entity, workForceComp, defaultLabelStyle, defaultTextButtonStyle, {GameScreenGUIManager.openEntityWindow(it)})
//
//                    workerListTable.add(workerTable).growX().spaceBottom(3f)
//                    workerListTable.row().growX()
//
//                    //Since this table gets updated on changes, make sure to keep the background if we reload this table
//                    //Also, we check the entity because the tables are new and won't match. We need to set the table again
//                    selectedWorkers.firstOrNull { it.worker == entity }?.run {
//                        workerTable.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.GRAY))) //Set the background of the selected table
//                        this.table = workerTable
//                    }
//
//                    workerTable.addListener(object : ClickListener() {
//                        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
//                            super.touchUp(event, x, y, pointer, button)
//                            val holdingShift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
//                            var existingWorker: SelectedWorkerAndTable? = null
//
//                            //If we're not holding shift, clear the list before starting again
//                            if (!holdingShift) {
//                                selectedWorkers.forEach { it.table.background = null } //Clear the background of each thing
//                                selectedWorkers.clear() //Clear the list
//
//                                //If we're holding shift, we need to check if the worker is already selected. If so, unselect it!
//                            } else {
//                                existingWorker = selectedWorkers.firstOrNull { it.worker == entity }
//                                if (existingWorker != null) { //If not null, unselect it
//                                    existingWorker.table.background = null
//                                    selectedWorkers.removeValue(existingWorker, true)
//
//                                    //Otherwise, add the new one
//                                }
//                            }
//
//                            if (existingWorker == null) {
//                                selectedWorkers.add(SelectedWorkerAndTable(entity, workerTable))
//                                workerTable.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.GRAY))) //Set the background of the selected table
//                            }
//                        }
//                    })
                }
            }

            val list = workerCompList.toList()
            GUIUtil.populateWorkerTable(list, selectedWorkers, workerListTable, defaultLabelStyle, defaultTextButtonStyle, GameScreenGUIManager)
        }

        populateWorkerTable()

        //An event to listen for a worker to be hired
        val updateEvent = GameEventSystem.subscribe<ReloadGUIEvent> { populateWorkerTable() }

        //Remember to remove this from the event system
        changedTabsFunc = { GameEventSystem.unsubscribe(updateEvent) }

        //Add our main scroll pane and the hiring button
        contentTable.add(mainScrollPane).grow().top().height(300f)
    }

    private fun createIncomeTable(){
        val incomeTable = Table()
        val titleTable = Table()

        val nameTitleLabel = Label("Name", defaultLabelStyle)
        nameTitleLabel.setAlignment(Align.center)
        nameTitleLabel.setFontScale(1.2f)
        val dailyIncomeTitle = Label("Daily Inc", defaultLabelStyle)
        dailyIncomeTitle.setAlignment(Align.center)
        dailyIncomeTitle.setFontScale(1.2f)
        val dailyTaxTitle = Label("Daily Tax", defaultLabelStyle)
        dailyTaxTitle.setAlignment(Align.center)
        dailyTaxTitle.setFontScale(1.2f)
        val totalIncomeTitle = Label("Total Inc", defaultLabelStyle)
        totalIncomeTitle.setAlignment(Align.center)
        totalIncomeTitle.setFontScale(1.2f)
        val totalTaxTitle = Label("Total Tax", defaultLabelStyle)
        totalTaxTitle.setAlignment(Align.center)
        totalTaxTitle.setFontScale(1.2f)
        val infoTableLabel = Label("Info", defaultLabelStyle)
        infoTableLabel.setAlignment(Align.center)
        infoTableLabel.setFontScale(1.2f)

        titleTable.add(nameTitleLabel).growX().uniformX()
        titleTable.add(dailyIncomeTitle).growX().uniformX()
        titleTable.add(dailyTaxTitle).growX().uniformX()
        titleTable.add(totalIncomeTitle).growX().uniformX()
        titleTable.add(totalTaxTitle).growX().uniformX()
        titleTable.add(infoTableLabel).growX().uniformX()

        val buildingListTable = Table()
        val scrollingList = ScrollPane(buildingListTable, defaultDarkScrollPaneStyle)

        fun populateBuildingListTable(sortType:String = ""){
            buildingListTable.clear()
            updateFuncsList.clear() //Clear the update funcs list. Be careful here, don't erase stuff we need!

            var list = Families.sellingItems.toList()

            //TODO Flesh this out
            when(sortType){
                "dailyInc" -> list = list.sortedWith(Comparator<Entity> { e1, e2 ->
                    val s1 = Mappers.selling[e1]
                    val s2 = Mappers.selling[e2]

                    s2.incomeDaily - s1.incomeDaily
                })
            }

            list.forEach { entity ->
                val buildingInfoTable = Table()

                val sc = Mappers.selling[entity]
                val ic = Mappers.identity[entity]

                val nameLabel = Label(ic.name, defaultLabelStyle).apply { setAlignment(Align.center) }
                val dailyIncomeLabel = Label(sc.incomeDaily.toString(), defaultLabelStyle).apply { setAlignment(Align.center) }
                val dailyTaxLabel = Label(sc.taxCollectedDaily.toString(), defaultLabelStyle).apply { setAlignment(Align.center) }
                val totalIncomeLabel = Label(sc.incomeTotal.toString(), defaultLabelStyle).apply { setAlignment(Align.center) }
                val totalTaxLabel = Label(sc.taxCollectedTotal.toString(), defaultLabelStyle).apply { setAlignment(Align.center) }
                val infoButton = TextButton("?", defaultTextButtonStyle)

                buildingInfoTable.add(nameLabel).growX().uniformX()
                buildingInfoTable.add(dailyIncomeLabel).growX().uniformX()
                buildingInfoTable.add(dailyTaxLabel).growX().uniformX()
                buildingInfoTable.add(totalIncomeLabel).growX().uniformX()
                buildingInfoTable.add(totalTaxLabel).growX().uniformX()
                buildingInfoTable.add(infoButton).growX().uniformX().size(16f)

                buildingListTable.add(buildingInfoTable).growX()
                buildingListTable.row()

                //Throw all these labels in an update function callback
                updateFuncsList.add {
                    dailyIncomeLabel.setText(sc.incomeDaily.toString())
                    dailyTaxLabel.setText(sc.taxCollectedDaily.toString())
                    totalIncomeLabel.setText(sc.incomeTotal.toString())
                    totalTaxLabel.setText(sc.taxCollectedTotal.toString())
                }
            }
        }

        dailyIncomeTitle.addListener(object:ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                populateBuildingListTable("dailyInc")
            }
        })

        populateBuildingListTable()

        incomeTable.add(titleTable).growX()
        incomeTable.row()
        incomeTable.add(scrollingList).growX()

        incomeTable.top()

        contentTable.add(incomeTable).grow().top().padTop(5f)
        contentTable.top()
    }

    override fun update(delta: Float) {
        super.update(delta)
    }

    override fun close() {
        GameEventSystem.unsubscribe(updatePopGraphEvent)
        super.close()
    }
}