package com.quickbite.economy.interfaces

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
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
open class GuiWindow(val guiManager: GameScreenGUIManager) {
    private val window: Window
    private val mainTable = Table()
    val tabTable = Table()
    val contentTable = Table()

    val updateList: Array<() -> Unit> = Array(5)
    val updateMap: HashMap<String, () -> Unit> = hashMapOf()
    var changedTabsFunc:()->Unit = {}

    val defaultLabelStyle = Label.LabelStyle(MyGame.manager["defaultFont", BitmapFont::class.java], Color.WHITE)

    val defaultButtonStyle = TextButton.TextButtonStyle()
    val darkBackgroundDrawable = NinePatchDrawable(NinePatch(MyGame.manager["dark_bar", Texture::class.java], 10, 10, 10, 10))
    val buttonBackgroundDrawable = NinePatchDrawable(NinePatch(MyGame.manager["button", Texture::class.java], 10, 10, 10, 10))

    init {
        defaultButtonStyle.up = NinePatchDrawable(NinePatch(MyGame.manager["button", Texture::class.java], 10, 10, 10, 10))
        defaultButtonStyle.font = MyGame.manager["defaultFont", BitmapFont::class.java]
        defaultButtonStyle.fontColor = Color.WHITE

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
        this.mainTable.add(contentTableScrollPane).expand().fill()

        //Make the window
        val windowBackground = NinePatchDrawable(NinePatch(MyGame.manager["dialog_box", Texture::class.java], 50, 50, 50, 50))
        val windowSkin = Window.WindowStyle(MyGame.manager["defaultFont", BitmapFont::class.java], Color.WHITE, windowBackground)

        //Window
        window = Window("", windowSkin)
        window.isMovable = true
        window.setSize(500f, 400f)
        window.setPosition(MathUtils.random(90f, 110f), MathUtils.random(90f, 110f))
        window.pad(20f, 10f, 10f, 10f)

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