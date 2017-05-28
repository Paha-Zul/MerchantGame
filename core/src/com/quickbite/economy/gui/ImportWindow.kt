package com.quickbite.economy.gui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.quickbite.economy.addChangeListener
import com.quickbite.economy.interfaces.GUIWindow
import com.quickbite.economy.managers.TownManager
import com.quickbite.economy.objects.SellingItemData
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 5/1/2017.
 * A window for importing items into the town
 */
class ImportWindow(guiManager: GameScreenGUIManager, val entity:Entity) : GUIWindow(guiManager){
    init{
        window.setSize(150f, 300f)

        tabTable.remove()

        fun loadIncomeList() {
            contentTable.clear()

            TownManager.getTown("Town").itemIncomeMap.values.filter { it.linkedToEntity == null }.forEach { income ->
                val itemIncomeButton = TextButton("", defaultTextButtonStyle)

                itemIncomeButton.setText("${income.itemName} - ${income.baseProductionAmtPerDay}/day")

                contentTable.add(itemIncomeButton).width(125f).spaceBottom(5f)
                contentTable.row()

                itemIncomeButton.addChangeListener { _, _ ->
                    income.linkedToEntity = entity
                    Util.addItemToEntitySelling(entity, income.itemName, SellingItemData.ItemSource.Import, "Town")
                    loadIncomeList()
                }
            }
        }

        loadIncomeList()
    }
}