package com.quickbite.economy.interfaces

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.MyGame
import com.quickbite.economy.gui.GameScreenGUIManager
import com.quickbite.economy.util.Util
import java.util.*

/**
 * Created by Paha on 3/9/2017.
 */
open class GUIWindow(val guiManager: GameScreenGUIManager) {
    /** The main GUI window that is movable*/
    protected val window: Window
    /** The main table that holds the tabTable and contentTable. Can be modified*/
    protected val mainTable = Table()
    /** The tab table for any tabs. Can be removed if not using tabs*/
    protected val tabTable = Table()
    /** The content table that sites under the tab table*/
    protected val contentTable = Table()

    /** A place to put actions to refresh GUI elements while a certain tab is open. Gets cleared on every window change.*/
    protected val updateFuncsList: Array<() -> Unit> = Array(5)
    protected val updateMap: HashMap<String, () -> Unit> = hashMapOf()
    protected var changedTabsFunc:()->Unit = {}

    protected val defaultLabelStyle = Label.LabelStyle(MyGame.defaultFont14, Color.WHITE)
    protected val defaultTextFieldStyle = TextField.TextFieldStyle()

    protected val defaultTextButtonStyle = TextButton.TextButtonStyle()
    protected val darkBackgroundDrawable = NinePatchDrawable(NinePatch(MyGame.manager["dark_bar", Texture::class.java], 10, 10, 10, 10))
    protected val buttonBackgroundDrawable = NinePatchDrawable(NinePatch(MyGame.manager["button", Texture::class.java], 10, 10, 10, 10))

    init {
        defaultTextButtonStyle.up = NinePatchDrawable(NinePatch(MyGame.manager["button", Texture::class.java], 10, 10, 10, 10))
        defaultTextButtonStyle.font = MyGame.defaultFont14
        defaultTextButtonStyle.fontColor = Color.WHITE

        defaultTextFieldStyle.font = MyGame.defaultFont14
        defaultTextFieldStyle.fontColor = Color.WHITE

        //Scroll pane for the main content window under the buttons.
        val scrollPaneStyle = ScrollPane.ScrollPaneStyle()
        scrollPaneStyle.vScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        scrollPaneStyle.vScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.BLACK)))
        scrollPaneStyle.hScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        scrollPaneStyle.hScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.BLACK)))

        tabTable.background = darkBackgroundDrawable

        val contentTableScrollPane = ScrollPane(contentTable, scrollPaneStyle)

        //Add the stuff to the main table
        this.mainTable.add(tabTable).expandX().fillX()
        this.mainTable.row()
        this.mainTable.add(contentTableScrollPane).grow()

        //Make the window
        val windowBackground = NinePatchDrawable(NinePatch(MyGame.manager["dialog_box", Texture::class.java], 50, 50, 50, 50))
        val windowSkin = Window.WindowStyle(MyGame.defaultFont20, Color.WHITE, windowBackground)

        //Window
        window = Window("", windowSkin)
//        window.getCell(window.titleLabel)
//        window.titleLabel.setFontScale(1.1f)
        window.isMovable = true
        window.setSize(500f, 400f)
        window.setPosition(MathUtils.random(90f, 110f), MathUtils.random(90f, 110f))
        window.pad(30f, 10f, 10f, 10f)

        window.add(this.mainTable).expand().fill()

        MyGame.stage.addActor(window)
    }

    open fun update(delta:Float){}

    fun toFront(){
        window.toFront()
    }

    open fun close(){
        mainTable.remove()
        window.remove()
        guiManager.closeWindow(this)
    }
}