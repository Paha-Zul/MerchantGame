package com.quickbite.economy.gui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.quickbite.economy.MyGame
import com.quickbite.economy.util.objects.SelectedWorkerAndTable
import com.quickbite.economy.util.Util

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
}