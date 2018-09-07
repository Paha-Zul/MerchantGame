package com.quickbite.economy.gui.entity

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.MyGame
import com.quickbite.economy.addChangeListener
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.components.FarmComponent
import com.quickbite.economy.interfaces.IEntCompWindow
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.util.Util

class EntFarmingWindow : IEntCompWindow{

    override fun open(window:EntityWindow, comp: Component, table: Table){
        var comp = comp as FarmComponent

        val scrollStyle = ScrollPane.ScrollPaneStyle()

        val listStyle = List.ListStyle(MyGame.defaultFont12, Color.WHITE, Color.WHITE,
                TextureRegionDrawable(TextureRegion(Util.createPixel(Color(0.3f, 0.3f, 0.3f, 0.3f)))))

        val dropdownStyle = SelectBox.SelectBoxStyle(MyGame.defaultFont12, Color.WHITE,
                TextureRegionDrawable(TextureRegion(Util.createPixel(Color(0f, 0f, 0f, 0f)))), scrollStyle, listStyle)

        val dropdown = SelectBox<DefinitionManager.PlantDef>(dropdownStyle)

        val list = mutableListOf<DefinitionManager.PlantDef>()
        DefinitionManager.plantDefMap.values.forEach {
            list.add(it)
        }
        dropdown.items = Array(list.toTypedArray())
        dropdown.selected = DefinitionManager.plantDefMap[comp.itemToGrow]

        dropdown.addChangeListener { _, _ ->
            comp.itemToGrow = dropdown.selected.toString()
        }

        table.add(dropdown).padTop(10f)

        table.top()
    }
}