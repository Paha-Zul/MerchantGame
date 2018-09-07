package com.quickbite.economy.gui.entity

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.utils.Align
import com.quickbite.economy.components.InventoryComponent
import com.quickbite.economy.gui.EntityWindowController
import com.quickbite.economy.interfaces.IEntCompWindow
import com.quickbite.economy.util.Mappers

class EntInventoryWindow : IEntCompWindow {
    override fun open(window: EntityWindow, comp: Component, table: Table) {
        var comp = comp as InventoryComponent

        val sellingComp = Mappers.selling[window.entity]

        //If we don't sell anything AND we output nothing... We want a minimal setting
        val minimal = sellingComp == null && comp.outputItems.containsKey("none")

        val contentsTable = Table()
        contentsTable.background = window.darkBackgroundDrawable

        val amountLabel = Label("Amount of items: ${comp.itemMap.size}", window.defaultLabelStyle)
        amountLabel.setFontScale(1f)

        val titleTable = Table()

        val itemNameTitle = Label("Name", window.defaultLabelStyle).apply { setAlignment(Align.center) }
        val itemAmountTitle = Label("Amount", window.defaultLabelStyle).apply { setAlignment(Align.center) }
        val itemSellingTitle = if(!minimal) Label("Selling", window.defaultLabelStyle).apply { setAlignment(Align.center) } else null
        val itemExportingTitle = if(!minimal) Label("Exporting", window.defaultLabelStyle).apply { setAlignment(Align.center) } else null

        titleTable.defaults().maxWidth(Value.percentWidth(0.25f, titleTable)).growX()

        titleTable.add().growX() //This is to pad the left with empty space
        titleTable.add(itemNameTitle).width(60f)
        titleTable.add(itemAmountTitle).width(60f)
        if(!minimal) { //We only want to add these if we need to
            titleTable.add(itemSellingTitle).width(60f)
            titleTable.add(itemExportingTitle).width(60f)
        }
        titleTable.add().growX() //Pads the right with empty space

        val listLabel = Label("Item List", window.defaultLabelStyle)
        listLabel.setFontScale(1f)
        listLabel.setAlignment(Align.center)

        //The main table...
        table.add(amountLabel).growX()
        table.row()
        table.add(contentsTable).growX()
        table.row()

        val populateItemTable = {
            contentsTable.clear()
            //This is a trick to get the correct widths for actors after this. Add blank cells and size them correctly.
            //This will cause the other rows to size the cell correctly without changing the actor
            contentsTable.add().growX() //Pads the left with empty space
            contentsTable.add().width(60f)
            contentsTable.add().width(60f)
            if(!minimal) {
                contentsTable.add().width(60f)
                contentsTable.add().width(60f)
            }
            contentsTable.add().growX() //Pads the right with empty space
            contentsTable.row()
            contentsTable.add(titleTable).colspan(6) //Just colspan a whole bunch here... doesn't really matter, at long as it's >4 or something
            contentsTable.row()

            //These will be the pinned items at the top of the inventory.
            val pinnedItemsSet = hashSetOf<String>()

            //First we check through and pin the base selling items
            EntityWindowController.makeBaseSellingPinnedItemsList(comp, sellingComp, contentsTable,
                    pinnedItemsSet, window.defaultLabelStyle, minimal)

            //Then we check through the output items and pin them to the top
            EntityWindowController.makeOutputPinnedItemsList(comp, sellingComp, contentsTable,
                    pinnedItemsSet, window.defaultLabelStyle, minimal)

            //Finally, make the rest of the items
            EntityWindowController.makeInventoryItemList(comp, sellingComp, contentsTable,
                    pinnedItemsSet, window.defaultLabelStyle, minimal)
        }

        populateItemTable()

        table.top()

        val listener = comp.addInventoryListener("all", {_,_,_ -> populateItemTable()})

//        updateFuncsList.add({populateItemTable()})

        window.changedTabsFunc = {
            comp.removeInventoryListener("all", listener)
        }
    }

}