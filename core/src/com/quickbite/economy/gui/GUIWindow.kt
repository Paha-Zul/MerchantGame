package com.quickbite.economy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.MyGame
import com.quickbite.economy.addChangeListener
import com.quickbite.economy.util.Util
import java.util.*

/**
 * Created by Paha on 3/9/2017.
 */
open class GUIWindow {
    /** The main GUI window that is movable*/
    val window: Window
    /** The main table that holds the tabTable and contentTable. Can be modified*/
    val mainTable = Table()
    /** The tab table for any tabs. Can be removed if not using tabs*/
    val tabTable = Table()
    /** The content table that sites under the tab table*/
    val contentTable = Table()

    /** A place to put actions to refresh GUI elements while a certain tab is open. Gets cleared on every window change.*/
    val updateFuncsList: Array<() -> Unit> = Array(5)
    val updateMap: HashMap<String, () -> Unit> = hashMapOf()
    var changedTabsFunc:()->Unit = {}

    public val defaultLabelStyle = Label.LabelStyle(MyGame.defaultFont14, Color.WHITE)
    public val defaultTitleLabelStyle = Label.LabelStyle(MyGame.defaultFont20, Color.WHITE)
    public val defaultTextFieldStyle = TextField.TextFieldStyle()

    public val defaultTextButtonStyle = TextButton.TextButtonStyle()
    public val darkBackgroundDrawable = NinePatchDrawable(NinePatch(MyGame.manager["dark_bar", Texture::class.java], 4, 4, 10, 4))
    public val darkBackgroundDrawableForTabs = NinePatchDrawable(NinePatch(MyGame.manager["dark_bar", Texture::class.java], 4, 4, 10, 4))
    public val buttonBackgroundDrawable = NinePatchDrawable(NinePatch(MyGame.manager["button", Texture::class.java], 10, 10, 10, 10))

    val barBackground = NinePatchDrawable(NinePatch(MyGame.manager["dialog_box_thin", Texture::class.java], 3, 3, 3, 3))

    val defaultLightScrollPaneStyle = ScrollPane.ScrollPaneStyle()
    val defaultDarkScrollPaneStyle = ScrollPane.ScrollPaneStyle()

    val TAB_HEIGHT = 30f

    init {
        defaultTextButtonStyle.up = NinePatchDrawable(NinePatch(MyGame.manager["button", Texture::class.java], 10, 10, 10, 10))
        defaultTextButtonStyle.font = MyGame.defaultFont14
        defaultTextButtonStyle.fontColor = Color.WHITE

        defaultTextFieldStyle.font = MyGame.defaultFont14
        defaultTextFieldStyle.fontColor = Color.WHITE

        defaultLightScrollPaneStyle.vScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        defaultLightScrollPaneStyle.vScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color(Color.BLACK))))
        defaultLightScrollPaneStyle.hScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        defaultLightScrollPaneStyle.hScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color(Color.BLACK))))

        defaultDarkScrollPaneStyle.background = darkBackgroundDrawable
        defaultDarkScrollPaneStyle.vScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        defaultDarkScrollPaneStyle.vScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color(Color.BLACK))))
        defaultDarkScrollPaneStyle.hScroll = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.WHITE)))
        defaultDarkScrollPaneStyle.hScrollKnob = TextureRegionDrawable(TextureRegion(Util.createPixel(Color(Color.BLACK))))

        tabTable.background = darkBackgroundDrawableForTabs

        val contentTableScrollPane = ScrollPane(contentTable, defaultLightScrollPaneStyle)
        contentTableScrollPane.setScrollingDisabled(true, false)

        //Add the stuff to the main table
        this.mainTable.add(tabTable).growX().height(30f)
        this.mainTable.row()
        this.mainTable.add(contentTableScrollPane).grow()

        //Make the window
        val windowBackground = NinePatchDrawable(NinePatch(MyGame.manager["dialog_box", Texture::class.java], 7, 7, 7, 7))
        val windowSkin = Window.WindowStyle(MyGame.defaultFont20, Color.WHITE, windowBackground)

        val exitButton = TextButton("x", defaultTextButtonStyle)
        exitButton.addChangeListener { _, _ -> close() }

        //Window
        window = Window("", windowSkin)
        window.isMovable = true
        window.touchable = Touchable.enabled
        window.setSize(500f, 400f)
        window.setPosition(MathUtils.random(90, 130).toFloat(), MathUtils.random(70, 130).toFloat())
        window.pad(30f, 10f, 10f, 10f)

        window.titleTable.add(exitButton).size(20f).right()
        window.add(this.mainTable).expand().fill().colspan(2)

        MyGame.stage.addActor(window)
    }

    open fun update(delta:Float){
        updateFuncsList.forEach { it() }
    }

    fun toFront(){
        window.toFront()
    }

    open fun close(){
        changedTabsFunc()
        mainTable.remove()
        window.remove()
        GameScreenGUIManager.closeWindow(this)
    }
}