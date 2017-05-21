package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.quickbite.economy.addChangeListener
import com.quickbite.economy.behaviour.Tasks
import com.quickbite.economy.components.WorkForceComponent
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ReloadGUIEvent

/**
 * Created by Paha on 5/20/2017.
 */
object GUIUtil {

    fun makeWorkerTable(entity:Entity, workforceComp:WorkForceComponent, labelStyle:Label.LabelStyle, textButtonStyle:TextButton.TextButtonStyle):Table{
        val worker = Mappers.worker[entity]
        val id = Mappers.identity[entity]

        var tasks = ""
        worker.taskList.forEachIndexed { index, task -> tasks += "${task[0].toUpperCase()}${if (index < worker.taskList.size - 1) "," else ""} " } //The complicated bit at the end controls the ending comma

        //The name and task label
        val workerNameLabel = Label(id.name, labelStyle)
        workerNameLabel.setAlignment(Align.center)
        val workerTasksLabel = Label(tasks, labelStyle)
        workerTasksLabel.setAlignment(Align.center)

        //The start and end time, with controls to handle each (slightly complicated)
        val startTimeTable = Table()
        val endTimeTable = Table()
        val workHoursStartLabel = Label("${worker.timeRange.first}", labelStyle)
        workHoursStartLabel.setAlignment(Align.center)
        val workHoursEndLabel = Label("${worker.timeRange.second}", labelStyle)
        workHoursEndLabel.setAlignment(Align.center)

        val workHourStartLessButton = TextButton("-", textButtonStyle)
        val workHourStartMoreButton = TextButton("+", textButtonStyle)
        val workHourEndLessButton = TextButton("-", textButtonStyle)
        val workHourEndMoreButton = TextButton("+", textButtonStyle)

        workHourStartLessButton.addChangeListener { _, _ ->
            worker.timeRange.first = Math.floorMod(worker.timeRange.first - 1, 24)
            workHoursStartLabel.setText("${worker.timeRange.first}")
        }

        workHourStartMoreButton.addChangeListener { _, _ ->
            worker.timeRange.first = Math.floorMod(worker.timeRange.first + 1, 24)
            workHoursStartLabel.setText("${worker.timeRange.first}")
        }

        workHourEndLessButton.addChangeListener { _, _ ->
            worker.timeRange.second = Math.floorMod(worker.timeRange.second - 1, 24)
            workHoursEndLabel.setText("${worker.timeRange.second}")
        }

        workHourEndMoreButton.addChangeListener { _, _ ->
            worker.timeRange.second = Math.floorMod(worker.timeRange.second + 1, 24)
            workHoursEndLabel.setText("${worker.timeRange.second}")
        }

        val salaryLabel = Label("${worker.dailyWage}", labelStyle)
        salaryLabel.setAlignment(Align.center)

        startTimeTable.add(workHourStartLessButton).size(16f)
        startTimeTable.add(workHoursStartLabel).space(0f, 5f, 0f, 5f).width(25f)
        startTimeTable.add(workHourStartMoreButton).size(16f)

        endTimeTable.add(workHourEndLessButton).size(16f)
        endTimeTable.add(workHoursEndLabel).space(0f, 5f, 0f, 5f).width(25f)
        endTimeTable.add(workHourEndMoreButton).size(16f)

        val removeWorkerButton = TextButton("x", textButtonStyle)
        removeWorkerButton.addListener(object: ClickListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                workforceComp.workersAvailable.removeValue(entity, true) //Remove the entity
                val bc = Mappers.behaviour[entity]
                bc.currTask = Tasks.leaveMap(bc.blackBoard) //Make the entity leave the map and be destroyed
                GameEventSystem.fire(ReloadGUIEvent())
            }
        })

        val workerTable = Table()

        workerTable.add(workerNameLabel).growX().uniformX()
        workerTable.add(workerTasksLabel).growX().uniformX()
        workerTable.add(startTimeTable).growX().uniformX()
        workerTable.add(endTimeTable).growX().uniformX()
        workerTable.add(salaryLabel).growX().uniformX()
        workerTable.add(removeWorkerButton).growX().uniformX().size(16f)

        return workerTable
    }
}