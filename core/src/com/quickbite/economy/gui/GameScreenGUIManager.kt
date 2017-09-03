package com.quickbite.economy.gui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.quickbite.economy.MyGame
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ItemAmountChangeEvent
import com.quickbite.economy.event.events.ItemSoldEvent
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.managers.TownManager
import com.quickbite.economy.objects.Town
import com.quickbite.economy.screens.GameScreen
import com.quickbite.economy.util.TimeOfDay
import com.quickbite.economy.util.Util
import java.util.Stack

/**
 * Created by Paha on 1/30/2017.
 */
object GameScreenGUIManager {
    lateinit var gameScreen:GameScreen
    val guiStack:Stack<GUIWindow> = Stack()

    private val labelStyle = Label.LabelStyle(MyGame.defaultFont20, Color.BLACK)
    val defaultLabelStyle = Label.LabelStyle(MyGame.defaultFont14, Color.WHITE)

    val bottomTable = Table()

    val topTable = Table()
    val itemTable = Table()

    lateinit var timeOfDayLabel:Label
    lateinit var populationLabel:Label
    lateinit var ratingLabel:Label

    enum class TooltipLocation{Mouse, Building}
    val toolTipTable:Table = Table()
    var showingTooltip = Tooltip()
    private var tooltipLocation = TooltipLocation.Mouse

    //Gotta be lazy cause Import won't be available right away
    private val myTown: Town by lazy {TownManager.getTown("Town")}

    init{
        toolTipTable.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color(0.1f, 0.1f, 0.1f, 0.7f))))
    }

    fun init(gameScreen:GameScreen){
        this.gameScreen = gameScreen

        val moneyLabel = Label("Gold: ${gameScreen.gameScreeData.playerMoney}", labelStyle)
        moneyLabel.setAlignment(Align.center)

        timeOfDayLabel = Label(TimeOfDay.toString(), labelStyle)
        timeOfDayLabel.setAlignment(Align.center)

        populationLabel = Label("", labelStyle)
        populationLabel.setAlignment(Align.center)

        ratingLabel = Label("", labelStyle)
        ratingLabel.setAlignment(Align.center)

        topTable.add(moneyLabel).width(200f)
        topTable.row()
        topTable.add(timeOfDayLabel).width(200f)
        topTable.row()
        topTable.add(populationLabel).width(200f)
        topTable.row()
        topTable.add(ratingLabel).width(200f)

        topTable.setPosition(MyGame.camera.viewportWidth/2f, MyGame.camera.viewportHeight - 50f)

        GameEventSystem.subscribe<ItemSoldEvent> {
            moneyLabel.setText("Gold: ${gameScreen.gameScreeData.playerMoney}")
        }

        setupSpawnEntityTable()
        setupItemTable()

        MyGame.stage.addActor(topTable)
        MyGame.stage.addActor(bottomTable)

        toolTipTable.touchable = Touchable.disabled
    }

    private fun setupItemTable(){
        fun populateItemTable() {
            itemTable.clear()
            myTown.totalSellingItemMap.forEach { name, amount ->
                val name = name.toLowerCase().replace(' ', '_')+"_icon"
                val icon = Image(MyGame.manager[name, Texture::class.java])
                val label = Label(amount.toString(), defaultLabelStyle)
                label.setFontScale(1f)
                label.setAlignment(Align.left)
                itemTable.add(icon).size(20f).padRight(2f)
                itemTable.add(label).width(25f)
                itemTable.row()
            }
        }

        populateItemTable()
        itemTable.setFillParent(true)
        itemTable.top().left()
        MyGame.stage.addActor(itemTable)

        //This is fired every time an item is added or removed from an inventory
        GameEventSystem.subscribe<ItemAmountChangeEvent> { (_, _) ->
            populateItemTable()
        }
    }

    /**
     * Takes all of the definitions from Json that are spawnable entities and builds a table of buttons
     */
    private fun setupSpawnEntityTable(){
        val ignoreEntity = hashSetOf("buyer", "worker", "wall", "hauler")

        val list = DefinitionManager.definitionMap.values.toList()
        list.forEach { def ->
            //TODO This is really just for quick disabling of spawning units for now...
            //If the ignoreEntity hashset contains the def name, don't add it to the buttons
            if(!ignoreEntity.contains(def.name.toLowerCase())) {
                val buttonStyle = ImageButton.ImageButtonStyle()
                buttonStyle.imageUp = TextureRegionDrawable(TextureRegion(MyGame.manager[def.graphicDef.graphicName, Texture::class.java]))

                val button = ImageButton(buttonStyle)

                button.addListener(object : ClickListener() {
                    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                        gameScreen.currentlySelectedType = def.name
                        return true
                    }
                })

                bottomTable.add(button).size(64f)
            }
        }

        bottomTable.width = MyGame.camera.viewportWidth
        bottomTable.height = 100f

        bottomTable.addListener(object:ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })
    }

    fun openHireWindow(workforceEntity:Entity){
        if(!guiStack.any { it is HireWorkerWindow })
            guiStack += HireWorkerWindow(workforceEntity)
    }

    fun openTownWindow() : TownWindow?{
        if(!guiStack.any{ it is TownWindow}) {
            val window = TownWindow()
            guiStack += window
            return window
        }

        return null
    }

    fun openImportWindow(entity:Entity){
        if(!guiStack.any{ it is ImportWindow})
            guiStack += ImportWindow(entity)
    }

    fun closeImportWindow(entity:Entity){
        guiStack.removeAll { (it as? ImportWindow)?.entity == entity }
    }

    fun removeTownWindow(){
        guiStack.removeIf {it is TownWindow}
    }

    /**
     * Opens an Entity window. If one already exists, brings it to the front
     * @param entity The Entity to display
     */
    fun openEntityWindow(entity:Entity){
        var exists = false
        guiStack.forEach{
            val window = it as? EntityWindow
            if(window?.entity == entity){
                window.toFront()
                exists = true
                return@forEach
            }
        }

        if(!exists)
            guiStack.add(EntityWindow(entity))
    }

    fun closeWindow(window: GUIWindow){
        guiStack.remove(window)
    }

    fun closeAllWindows(){
        guiStack.toList().forEach { it.close() } //We copy this using toList() so we don't have a concurrent modification...
        guiStack.clear()
    }

    fun update(delta:Float){
        guiStack.forEach { it.update(delta) }

        timeOfDayLabel.setText("D: ${TimeOfDay.day}, T: $TimeOfDay")
        populationLabel.setText("Pop: ${myTown.population.toInt()}")
        ratingLabel.setText("N: ${myTown.needsRating}, L: ${myTown.luxuryRating}")

        displayTooltip()
    }

    /**
     * Displays the tooltip (if there is one currently open)
     */
    private fun displayTooltip(){
        if(!showingTooltip.showing) return

        val position = when(tooltipLocation){
            TooltipLocation.Mouse -> {
                Vector2(Gdx.input.x.toFloat() - toolTipTable.width/2f, MyGame.camera.viewportHeight - Gdx.input.y.toFloat())
            }
            TooltipLocation.Building -> {
                Vector2(Gdx.input.x.toFloat() - toolTipTable.width/2f, Gdx.input.y.toFloat())
            }
        }

        toolTipTable.setPosition(position.x, position.y)
    }

    /**
     * Triggers the tooltip to show
     * @param location The location for it to display like at the mouse or building hovered over
     */
    fun startShowingTooltip(location:TooltipLocation){
        showingTooltip.showing = true
        tooltipLocation = location
        toolTipTable.pack()
        MyGame.stage.addActor(toolTipTable)
    }

    /**
     * Stops showing the tooltip
     */
    fun stopShowingTooltip(){
        showingTooltip.showing = false
        toolTipTable.remove()
        if(showingTooltip.entity != null){
            GameEventSystem.unsubscribe(showingTooltip.event!!)
        }

        showingTooltip.entity = null
        showingTooltip.event = null
    }

    data class Tooltip(var showing:Boolean = false, var entity:Entity?=null, var event:GameEventSystem.GameEventRegistration? = null)
}