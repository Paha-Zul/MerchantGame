package com.quickbite.economy.gui.entity

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.utils.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.quickbite.economy.MyGame
import com.quickbite.economy.addChangeListener
import com.quickbite.economy.components.*
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ItemSoldEvent
import com.quickbite.economy.event.events.ReloadGUIEvent
import com.quickbite.economy.gui.EntityWindowController
import com.quickbite.economy.gui.GUIUtil
import com.quickbite.economy.gui.GUIWindow
import com.quickbite.economy.gui.GameScreenGUIManager
import com.quickbite.economy.gui.widgets.Graph
import com.quickbite.economy.gui.widgets.ProductionMap
import com.quickbite.economy.input.InputController
import com.quickbite.economy.interfaces.IEntCompWindow
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.systems.RenderSystem
import com.quickbite.economy.util.Factory
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util
import com.quickbite.economy.util.objects.SelectedWorkerAndTable

class EntSellingWindow : IEntCompWindow {
    override fun open(window: EntityWindow, comp: Component, table: Table) {
        var comp = comp as SellingItemsComponent

        val taxRateTable = Table()
        taxRateTable.background = window.darkBackgroundDrawable

        val sellItemsMainTable = Table()
        sellItemsMainTable.background = window.darkBackgroundDrawable

        val sellHistoryTable = Table()
        sellHistoryTable.background = window.darkBackgroundDrawable

        //Tax title
        val taxLabel = Label("Tax", window.defaultTitleLabelStyle)
        taxLabel.setAlignment(Align.center)

        //Set up the selling title
        val sellLabel = Label("Active", window.defaultTitleLabelStyle)
        sellLabel.setAlignment(Align.center)

        val historyTitleLabel = Label("History", window.defaultTitleLabelStyle)
        historyTitleLabel.setAlignment(Align.center)

        //This will populate the table of items being sold

        //Initially call this function to populate the items table
        GUIUtil.populateItemsTable(comp, sellItemsMainTable, window.defaultLabelStyle, window.defaultTextButtonStyle)

        /**--- Tax rate section --- **/

        val taxRateLabel = Label("${comp.taxRate}", window.defaultLabelStyle)
        taxRateLabel.setFontScale(1f)
        taxRateLabel.setAlignment(Align.center)

        val lessTaxButton = TextButton("-", window.defaultTextButtonStyle)
        lessTaxButton.label.setFontScale(1f)

        val moreTaxButton = TextButton("+", window.defaultTextButtonStyle)
        moreTaxButton.label.setFontScale(1f)

        taxRateTable.add(lessTaxButton).size(16f)
        taxRateTable.add(taxRateLabel).width(50f)
        taxRateTable.add(moreTaxButton).size(16f)

        //If we are also reselling, add this stuff
        if(comp.isReselling)
            setupResellingStuff(window, table, comp)


        //The listener for when we hit the less tax button
        lessTaxButton.addListener(object: ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                //The 0.5 amd 4.5 are because of rounding for bad floats
                var adj = 0.5
                if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                    adj = 4.5

                var rate = (comp.taxRate*100 - adj).toInt()
                if(rate < 0)
                    rate = 0

                comp.taxRate = rate/100f

                taxRateLabel.setText("${comp.taxRate}")
            }
        })

        //The callback for hitting the more tax button
        moreTaxButton.addListener(object: ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                //The 1.5 amd 5.5 are because of rounding for bad floats
                var adj = 1.5
                if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                    adj = 5.5

                var rate = (comp.taxRate*100 + adj).toInt()
                if(rate > 100)
                    rate = 100

                comp.taxRate = rate/100f

                taxRateLabel.setText("${comp.taxRate}")
            }
        })

        table.top()

        //Add all the stuff to the table
        table.add(taxLabel).growX().padTop(5f) //The taxCollected rate
        table.row().growX().spaceTop(0f)
        table.add(taxRateTable).growX().padTop(5f) //The taxCollected rate
        table.row().growX().spaceTop(5f)
        table.add(sellLabel).growX() //What we are selling
        table.row().growX().spaceTop(5f)
        table.add(sellItemsMainTable).growX() //What we are selling
        table.row().growX().spaceTop(5f)
        table.add(historyTitleLabel).growX() //The history table
        table.row().grow().spaceTop(5f) //Push everything up!
        table.add(sellHistoryTable).growX() //The history table
        table.row().grow().spaceTop(5f) //Push everything up!

        table.invalidateHierarchy()
        table.validate()
        table.layout()
        table.act(0.016f)

        //Call the history table function to populate the history
        GUIUtil.populateHistoryTable(comp, sellHistoryTable, window.defaultLabelStyle, table.width)

        //Put the history function into our update map
        window.updateMap.put("sellHistory", { GUIUtil.populateHistoryTable(comp, sellHistoryTable, window.defaultLabelStyle, table.width) })

        window.updateFuncsList.add { GUIUtil.populateItemsTable(comp, sellItemsMainTable, window.defaultLabelStyle, window.defaultTextButtonStyle) }

        //Put in the event system
        val entID = Mappers.identity[window.entity].uniqueID
        val entityEvent = GameEventSystem.subscribe<ItemSoldEvent>({ GUIUtil.populateHistoryTable(comp,
                sellHistoryTable, window.defaultLabelStyle, table.width) }, entID) //Subscribe to the entity selling an item

        window.changedTabsFunc = {
            GameEventSystem.unsubscribe(entityEvent) //Unsubscribe when we leave this tab
        }
    }

    private fun setupResellingStuff(window:EntityWindow, table: Table, comp: SellingItemsComponent){
        //The link button for linking together this shop and a store
        val linkButton = TextButton("Link", window.defaultTextButtonStyle)
        val importButton = TextButton("Import", window.defaultTextButtonStyle)

        val buttonTable = Table()
        buttonTable.add(linkButton).spaceRight(50f) //The link button
        buttonTable.add(importButton) //The import button

        table.add(buttonTable).padTop(20f)
        table.row()

        //The listener for the link button
        linkButton.addListener(object: ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                //TODO Probably want to clean this up
                GameScreenGUIManager.gameScreen.inputHandler.linkingAnotherEntity = true
                GameScreenGUIManager.gameScreen.inputHandler.linkingEntityCallback = { ent ->
                    InputController.linkEntityForReselling(ent, window.entity)
                }
                RenderSystem.linkToArrow.active = true
                RenderSystem.linkToArrow.start.set(Mappers.transform[window.entity].position)
            }
        })

        importButton.addChangeListener { _, _ ->
            GameScreenGUIManager.openImportWindow(window.entity)
        }
    }
}