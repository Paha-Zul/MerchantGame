package com.quickbite.economy.util.objects

import com.badlogic.ashley.core.Entity
import com.quickbite.economy.components.GraphicComponent
import com.quickbite.economy.components.TransformComponent
import com.quickbite.economy.managers.DefinitionManager

class ShadowObject {
    var entity: Entity? = null
    var transformComp:TransformComponent? = null
    var graphicComp:GraphicComponent? = null
    var def:DefinitionManager.ConstructionDef? = null
    var canBuild = false
}