package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array


/**
 * Created by Paha on 3/15/2017.
 */
data class WorkerTaskData(var entity: Entity, var taskList: Array<String>, val timeRange:Pair<Int, Int>, val days:Array<Int>)