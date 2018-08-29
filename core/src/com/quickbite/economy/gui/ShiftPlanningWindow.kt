package com.quickbite.economy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.WorkForceComponent
import com.quickbite.economy.util.Util

class ShiftPlanningWindow(private val workforce:WorkForceComponent) {
    //Names on the left
    //Names on the buttons or length of shift?
    //Times on top/bottom
    //Click and drag buttons/sliders around
    //Click and dragging right side expands/shrinks button
    //Everything snaps to hours increments

    fun openTable(){
        val bp = Util.createPixel(Color.BLACK)
        val drawable = TextureRegionDrawable(TextureRegion(bp))
        val textButtonStyle = TextButton.TextButtonStyle(drawable, drawable, drawable, MyGame.defaultFont12)

        val table = Table()

        val insideTable = Table()
        insideTable.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.BROWN)))

        for(i in 0 until 2) {
            val shiftButton = TextButton("", textButtonStyle)

            var moving = false
            var resizing = false

            shiftButton.addListener(object : DragListener() {
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    if (x >= shiftButton.width * 0.8f)
                        resizing = true
                    else
                        moving = true

                    return true
                }

                override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                    if (resizing)
                        shiftButton.width = ((x.toInt() + 25) / 50) * 50.toFloat()
                    if (moving) {
                        val x1 = ((shiftButton.x + x).toInt() / 50) * 50
                        shiftButton.setPosition(x1.toFloat(), shiftButton.y)
                    }
                }

                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    resizing = false
                    moving = false
                }
            })

            insideTable.add(shiftButton).size(100f, 25f).growX()
            insideTable.row()
        }

        table.add(insideTable).size(800f, 400f)
        table.row()
        table.setFillParent(true)
        table.debugAll()

        MyGame.stage.addActor(table)
    }

}