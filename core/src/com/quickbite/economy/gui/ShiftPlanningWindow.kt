package com.quickbite.economy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
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
        val button = TextButton("", textButtonStyle)


    }

}