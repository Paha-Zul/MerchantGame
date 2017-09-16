package com.quickbite.economy.gui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.InventoryComponent
import com.quickbite.economy.components.SellingItemsComponent
import com.quickbite.economy.util.Util
import com.quickbite.economy.util.objects.SelectedWorkerAndTable
import com.quickbite.economy.util.objects.SellingState

/**
 * Created by Paha on 5/23/2017.
 * A Controller for the EntityWindow class to separate some logic.
 */
object EntityWindowController {

    /**
     * Adds a certain task to all selected workers (selectedWorkers)
     * @param taskNameText The task name
     * @param selectedWorkers The workers that are currently selected
     */
    fun addTaskToWorkers(taskNameText:String, selectedWorkers:com.badlogic.gdx.utils.Array<SelectedWorkerAndTable>,
                         workforceEntity:Entity){

        //For each of the currently selected workers...
        selectedWorkers.forEach { (entity, _) ->
            Util.toggleTaskOnWorker(entity, workforceEntity, taskNameText) //Toggle it
        }
    }

    /**
     * Modifies the worker selection in the worker table
     * @param selectedWorkers The currently selected workers
     * @param entityBeingSelected The current entity being selected
     * @param workerTable The table of the worker with all its info
     */
    fun changeWorkerSelectionInTable(selectedWorkers: com.badlogic.gdx.utils.Array<SelectedWorkerAndTable>, entityBeingSelected: Entity, workerTable:Table){
        val holdingShift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
        var existingWorker: SelectedWorkerAndTable? = null

        //If we're not holding shift, clear the list before starting again
        if(!holdingShift) {
            selectedWorkers.forEach { it.table.background = NinePatchDrawable(NinePatch(MyGame.manager["dialog_box_thin", Texture::class.java], 3, 3, 3, 3)) } //Clear the background of each thing
            selectedWorkers.clear() //Clear the list

        //If we're holding shift, we need to check if the worker is already selected. If so, unselect it!
        }else{
            existingWorker = selectedWorkers.firstOrNull { it.worker == entityBeingSelected }
            if(existingWorker != null) { //If not null, unselect it
                existingWorker.table.background = NinePatchDrawable(NinePatch(MyGame.manager["dialog_box_thin", Texture::class.java], 3, 3, 3, 3))
                selectedWorkers.removeValue(existingWorker, true)

                //Otherwise, add the new one
            }
        }

        //If the existing worker is null, add the selected worker and table to the selectedWorkers list. And set the background!
        if(existingWorker == null) {
            selectedWorkers.add(SelectedWorkerAndTable(entityBeingSelected, workerTable))
            workerTable.background = NinePatchDrawable(NinePatch(MyGame.manager["dialog_box_thin_selected", Texture::class.java], 3, 3, 3, 3)) //Set the background of the selected table
        }
    }

    fun makeBaseSellingPinnedItemsList(invComp: InventoryComponent, sellingComp: SellingItemsComponent?, contentsTable:Table, pinnedItemsSet:HashSet<String>, labelStyle:Label.LabelStyle){
        //First we check through and pin the base selling items
        sellingComp?.baseSellingItems?.forEach {
            pinnedItemsSet.add(it.itemName) //Add it
            //If the item is in the current selling items, it's selling. If not, then it's available
            val sellState = if(sellingComp.currSellingItems.firstOrNull { item -> item.itemName == it.itemName} != null) SellingState.Active else SellingState.Available
            GUIUtil.makeInventoryItemTable(it.itemName, invComp.getItemAmount(it.itemName), contentsTable, labelStyle,
                    sellState, sellingComp.currSellingItems, invComp.outputItems)
        }
    }

    fun makeOutputPinnedItemsList(invComp: InventoryComponent, sellingComp: SellingItemsComponent?, contentsTable:Table, pinnedItemsSet:HashSet<String>, labelStyle:Label.LabelStyle){
        invComp.outputItems.forEach { (key, _) ->
            if(key != "all" && !pinnedItemsSet.contains(key)) {
                pinnedItemsSet.add(key)
                //If the item is in the current selling items, it's selling. If not, then it's available
                val sellState = when {
                    sellingComp == null -> SellingState.Unable
                    sellingComp.currSellingItems.firstOrNull { it.itemName == key } != null -> SellingState.Active
                    else -> SellingState.Available
                }
                GUIUtil.makeInventoryItemTable(key, invComp.getItemAmount(key), contentsTable, labelStyle,
                        sellState, sellingComp?.currSellingItems, invComp.outputItems)
            }
        }
    }

    fun makeInventoryItemList(invComp: InventoryComponent, sellingComp: SellingItemsComponent?, contentsTable:Table, pinnedItemsSet:HashSet<String>, labelStyle:Label.LabelStyle){
        //Since we handled both the base selling items and specific output items, here we either set the rest to available or unable
        //This basically says if we output "all", set everything to available. Otherwise, set to unable
        val sellStateForRest = if(invComp.outputItems.contains("all") && sellingComp != null) SellingState.Available else SellingState.Unable

        invComp.itemMap.values.forEach { (itemName, itemAmount) ->
            if(!pinnedItemsSet.contains(itemName)) {
                val sellState = when {
                //If the item is in teh current selling items, set it to already selling
                    sellingComp?.currSellingItems?.firstOrNull { it.itemName == itemName } != null -> SellingState.Active
                    itemName == "gold" -> SellingState.Unable //Check for gold...
                    else -> sellStateForRest //Use the predefined state
                }
                GUIUtil.makeInventoryItemTable(itemName, itemAmount, contentsTable, labelStyle,
                        sellState, sellingComp?.currSellingItems, invComp.outputItems)
            }
        }
    }
}