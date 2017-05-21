package com.quickbite.economy.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
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
import com.quickbite.economy.interfaces.GUIWindow
import com.quickbite.economy.managers.TownManager
import com.quickbite.economy.objects.SelectedWorkerAndTable
import com.quickbite.economy.util.Families
import com.quickbite.economy.util.GUIUtil
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 4/9/2017.
 */
class TownWindow(guiManager: GameScreenGUIManager) : GUIWindow(guiManager) {
    lateinit var updatePopGraphEvent:GameEventSystem.GameEventRegistration
    private val town by lazy { TownManager.getTown("Town") }
    private var selectedWorkers = Array<SelectedWorkerAndTable>()

    init{
        window.titleLabel.setText("Town")
        initTabs()
        changeTabs("pop")
    }

    private fun initTabs(){
        val popButton = TextButton("Pop", defaultTextButtonStyle)
        val workersButton = TextButton("Workers", defaultTextButtonStyle)
        val exitButton = TextButton("x", defaultTextButtonStyle)

        popButton.addChangeListener { _, _ -> changeTabs("pop") }
        workersButton.addChangeListener { _, _ -> changeTabs("workers") }

        exitButton.addListener(object: ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                close()
            }
        })

        tabTable.add(popButton).right().size(75f, 32f).spaceRight(5f)
        tabTable.add(workersButton).right().size(75f, 32f).spaceRight(5f)
        tabTable.add().expandX().fillX()
        tabTable.add(exitButton).right().size(32f)
    }

    private fun changeTabs(type:String){
        changedTabsFunc()
        changedTabsFunc = {}

        updateFuncsList.clear()

        when(type){
            "pop" -> setupPopTab()
            "workers" -> setupWorkersTable()
        }
    }

    private fun setupPopTab(){
        contentTable.clear()

        val style = Graph.GraphStyle(TextureRegionDrawable(TextureRegion(Util.createPixel(Color.BLACK))), Color.BLACK, MyGame.defaultFont14)
        style.graphBackground = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.DARK_GRAY)))
        style.lineThickness = 2f

        val popTitleLabel = Label("Population:", defaultLabelStyle)
        val popAmtLabel = Label("${town.population.toInt()}", defaultLabelStyle)

        //TODO These should probably become progress bars
        val needsTitleLabel = Label("Needs:", defaultLabelStyle)
        val needsAmtLabel = Label("${town.needsRating}", defaultLabelStyle)

        val luxuryTitleLabel = Label("Luxury:", defaultLabelStyle)
        val luxuryAmtLabel = Label("${town.luxuryRating}", defaultLabelStyle)

        val graph = Graph(TownManager.getTown("Town").populationHistory.queue.toList(), 100, style)

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

    fun setupWorkersTable(){
        //TODO This doesn't update if it's open a building is destroyed, hmm....
        contentTable.clear()

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

            val titleTable = Table()
            workerListTable.add(titleTable).growX()
            workerListTable.row().growX()

            //Add all the titles....
            titleTable.add(nameTitleLabel).growX().uniformX()
            titleTable.add(currTaskTitleLabel).growX().uniformX()
            titleTable.add(startTimeTitleLAbel).growX().uniformX()
            titleTable.add(endTimeTitleLabel).growX().uniformX()
            titleTable.add(salaryTitleLabel).growX().uniformX()
            titleTable.add(fireTitleLabel).growX().uniformX()

            Families.workBuildings.forEach { workBuildingEnt ->
                val workForceComp = Mappers.workforce[workBuildingEnt]

                workForceComp.workersAvailable.forEach { entity ->
                    val workerTable = GUIUtil.makeWorkerTable(entity, workForceComp, defaultLabelStyle, defaultTextButtonStyle)

                    workerListTable.add(workerTable).growX()
                    workerListTable.row().growX()

                    //Since this table gets updated on changes, make sure to keep the background if we reload this table
                    //Also, we check the entity because the tables are new and won't match. We need to set the table again
                    selectedWorkers.firstOrNull { it.worker == entity }?.run {
                        workerTable.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.GRAY))) //Set the background of the selected table
                        this.table = workerTable
                    }

                    workerTable.addListener(object : ClickListener() {
                        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                            super.touchUp(event, x, y, pointer, button)
                            val holdingShift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                            var existingWorker: SelectedWorkerAndTable? = null

                            //If we're not holding shift, clear the list before starting again
                            if (!holdingShift) {
                                selectedWorkers.forEach { it.table.background = null } //Clear the background of each thing
                                selectedWorkers.clear() //Clear the list

                                //If we're holding shift, we need to check if the worker is already selected. If so, unselect it!
                            } else {
                                existingWorker = selectedWorkers.firstOrNull { it.worker == entity }
                                if (existingWorker != null) { //If not null, unselect it
                                    existingWorker.table.background = null
                                    selectedWorkers.removeValue(existingWorker, true)

                                    //Otherwise, add the new one
                                }
                            }

                            if (existingWorker == null) {
                                selectedWorkers.add(SelectedWorkerAndTable(entity, workerTable))
                                workerTable.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.GRAY))) //Set the background of the selected table
                            }
                        }
                    })
                }
            }
        }

        populateWorkerTable()

        //An event to listen for a worker to be hired
        val updateEvent = GameEventSystem.subscribe<ReloadGUIEvent> { populateWorkerTable() }

        //Remember to remove this from the event system
        changedTabsFunc = { GameEventSystem.unsubscribe(updateEvent) }

        //Add our main scroll pane and the hiring button
        contentTable.add(mainScrollPane).grow().top().height(300f)
    }

    override fun update(delta: Float) {
        super.update(delta)
    }

    override fun close() {
        GameEventSystem.unsubscribe(updatePopGraphEvent)
        super.close()
    }
}