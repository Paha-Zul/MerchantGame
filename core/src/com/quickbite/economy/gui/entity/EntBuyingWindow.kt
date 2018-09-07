package com.quickbite.economy.gui.entity

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.quickbite.economy.components.BuyerComponent
import com.quickbite.economy.interfaces.IEntCompWindow

class EntBuyingWindow : IEntCompWindow {
    override fun open(window: EntityWindow, comp: Component, table: Table) {
        var comp = comp as BuyerComponent;

        val buyingTable = Table()

        val itemNameTitle = Label("Item", window.defaultLabelStyle)
        itemNameTitle.setFontScale(1f)

        val itemAmountTitle = Label("Amount", window.defaultLabelStyle)
        itemAmountTitle.setFontScale(1f)

        val necessityRatingLabel = Label("Needs: ${comp.needsSatisfactionRating}", window.defaultLabelStyle)
        necessityRatingLabel.setFontScale(1f)

        val luxuryRatingLabel = Label("Luxury: ${comp.luxurySatisfactionRating}", window.defaultLabelStyle)
        luxuryRatingLabel.setFontScale(1f)

        val populateTableFunc = {
            buyingTable.clear()

            luxuryRatingLabel.setText("Luxury: ${comp.luxurySatisfactionRating}")
            necessityRatingLabel.setText("Needs: ${comp.needsSatisfactionRating}")

            buyingTable.add(necessityRatingLabel)
            buyingTable.add(luxuryRatingLabel)
            buyingTable.row()
            buyingTable.add(itemNameTitle)
            buyingTable.add(itemAmountTitle)

            comp.buyList.forEach { pair ->
                val itemName = Label(pair.itemName, window.defaultLabelStyle)
                itemName.setFontScale(1f)
                val itemAmount = Label("${pair.itemAmount}", window.defaultLabelStyle)
                itemAmount.setFontScale(1f)

                buyingTable.row()
                buyingTable.add(itemName)
                buyingTable.add(itemAmount)
            }
        }

        populateTableFunc()

        window.updateFuncsList.add {  populateTableFunc() }

        table.add(buyingTable)

//        updateFuncsList.add(UpdateLabel(nameLabel, { label -> label.setText("CurrTaskName: ${comp.currTaskName}")}))
    }
}