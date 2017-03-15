package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array


/**
 * Created by Paha on 3/15/2017.
 */
data class EntityTasksLink(var entity: Entity, var taskLink: Array<String>)