package com.quickbite.economy.gui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.quickbite.economy.objects.SelectedWorkerAndTable
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
     * @param workerTaskLimit The limit of the worker task
     */
    fun addTasksToWorkers(taskNameText:String, selectedWorkers:com.badlogic.gdx.utils.Array<SelectedWorkerAndTable>,
                          workforceEntity:Entity){

        //For each of the currently selected workers...
        selectedWorkers.forEach { (entity, _) ->
            Util.toggleTaskOnWorker(entity, workforceEntity, taskNameText)
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
        var existingWorker:SelectedWorkerAndTable? = null

        //If we're not holding shift, clear the list before starting again
        if(!holdingShift) {
            selectedWorkers.forEach { it.table.background = null } //Clear the background of each thing
            selectedWorkers.clear() //Clear the list

            //If we're holding shift, we need to check if the worker is already selected. If so, unselect it!
        }else{
            existingWorker = selectedWorkers.firstOrNull { it.worker == entityBeingSelected }
            if(existingWorker != null) { //If not null, unselect it
                existingWorker.table.background = null
                selectedWorkers.removeValue(existingWorker, true)

                //Otherwise, add the new one
            }
        }

        //If the existing worker is null, add the selected worker and table to the selectedWorkers list. And set the background!
        if(existingWorker == null) {
            selectedWorkers.add(SelectedWorkerAndTable(entityBeingSelected, workerTable))
            workerTable.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color.GRAY))) //Set the background of the selected table
        }
    }
}