package com.quickbite.economy.interfaces

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.quickbite.economy.gui.entity.EntityWindow

interface IEntCompWindow {

    fun open(window: EntityWindow, comp:Component, table: Table)
}