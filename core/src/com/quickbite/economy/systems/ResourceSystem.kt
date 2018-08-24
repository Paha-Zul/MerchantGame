package com.quickbite.economy.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.graphics.Texture
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.ResourceComponent
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 6/11/2017.
 */
class ResourceSystem(val interval:Float): IntervalIteratingSystem(Family.all(ResourceComponent::class.java).get(), interval) {
    override fun processEntity(ent: Entity?) {
        val rc = Mappers.resource[ent]
        if(!rc.canRegrow || !rc.harvested) return //If it can't regrow or it's not harvested, don't bother...

        rc.nextRegrowTime -= interval //Subtract the interval this system runs on from the next regrow time

        //When we are ready to regrow...
        if(rc.nextRegrowTime <=0){
            rc.harvested = false
            rc.currResourceAmount = rc.resourceAmount
            rc.numCurrentHarvesters = 0

            val resourceDef = DefinitionManager.definitionMap[Mappers.identity[ent].name.toLowerCase()]!! //Get the resource definition using the resources name from its identity
            val gc = Mappers.graphic[ent] //Get its graphic component
            gc.sprite.setRegion(MyGame.manager[resourceDef.graphicDef.graphicName, Texture::class.java]) //Set it to the graphic
        }

    }
}