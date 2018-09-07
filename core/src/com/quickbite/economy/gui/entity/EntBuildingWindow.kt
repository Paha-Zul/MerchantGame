package com.quickbite.economy.gui.entity

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.quickbite.economy.components.BuildingComponent
import com.quickbite.economy.interfaces.IEntCompWindow

class EntBuildingWindow : IEntCompWindow {

    override fun open(window: EntityWindow, comp: Component, table: Table) {
        var comp = comp as BuildingComponent;

        val typeLabel = Label("Type: ${comp.buildingType}", window.defaultLabelStyle)
        typeLabel.setFontScale(1f)
        val queueLabel = Label("Queue size: ${comp.unitQueue.size}", window.defaultLabelStyle)
        queueLabel.setFontScale(1f)

        table.add(typeLabel).expandX().fillX()
        table.row()
        table.add(queueLabel).expandX().fillX()

        window.updateFuncsList.add {typeLabel.setText("Type: ${comp.buildingType}")}
        window.updateFuncsList.add {queueLabel.setText("Queue size: ${comp.unitQueue.size}")}
    }

}