package com.quickbite.economy.gui

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.quickbite.economy.MyGame
import com.quickbite.economy.addChangeListener
import com.quickbite.economy.components.DebugDrawComponent
import com.quickbite.economy.screens.GameScreen
import com.quickbite.economy.systems.DebugDrawSystem

object EditorGUI {
    enum class EDITING_STATE{
        None, Center, Entrance, BlockGrid, ClearGrid
    }

    var editingState = EDITING_STATE.None
    val mainTable = Table()

    fun openEditor(){
        val table = Table()

        val buttonStyle = TextButton.TextButtonStyle()
        buttonStyle.font = MyGame.defaultFont12

        val editCenterButton = TextButton("Center", buttonStyle)
        val editEntranceButton = TextButton("Entrance", buttonStyle)
        val blockGridButton = TextButton("Block Grid", buttonStyle)
        val clearGridButton = TextButton("Clear Grid", buttonStyle)

        table.add(editCenterButton).row()
        table.add(editEntranceButton).row()
        table.add(blockGridButton).row()
        table.add(clearGridButton).row()

        mainTable.setFillParent(true)
        mainTable.add().expandX().fillX()
        mainTable.add(table).right().center()

        editCenterButton.addChangeListener { _, _ ->
            editingState = EDITING_STATE.Center
            DebugDrawComponent.clearStates()
            DebugDrawComponent.GLOBAL_DEBUG_CENTER = true
        }

        editEntranceButton.addChangeListener { _, _ ->
            editingState = EDITING_STATE.Entrance
            DebugDrawComponent.clearStates()
            DebugDrawComponent.GLOBAL_DEBUG_ENTRANCE = true
        }

        blockGridButton.addChangeListener { _, _ ->
            DebugDrawComponent.clearStates()
            editingState = EDITING_STATE.BlockGrid
            GameScreen.showGrid = true
        }

        clearGridButton.addChangeListener { _, _ ->
            DebugDrawComponent.clearStates()
            editingState = EDITING_STATE.ClearGrid
            GameScreen.showGrid = true
        }

        MyGame.stage.addActor(mainTable)
    }

    fun closeEditor(){
        if(mainTable.hasParent()) {
            editingState = EDITING_STATE.None
            DebugDrawComponent.clearStates()
            mainTable.clear()
            mainTable.remove()
        }
    }
}