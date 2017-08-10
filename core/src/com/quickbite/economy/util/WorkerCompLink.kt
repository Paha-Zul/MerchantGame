package com.quickbite.economy.util

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.components.WorkForceComponent

data class WorkerCompLink(val entity:Entity, val workForceComp:WorkForceComponent)