package com.quickbite.economy.util.objects

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.scenes.scene2d.ui.Table

/**
 * Created by Paha on 5/20/2017.
 * Creates a link between an Entity (worker) and a table. Used for easy updating in GUIs
 * @param worker The Entity worker
 * @param table The Table object for the worker
 */
data class SelectedWorkerAndTable(var worker: Entity, var table: Table)
