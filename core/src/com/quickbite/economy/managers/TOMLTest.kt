package com.quickbite.economy.managers

import com.badlogic.gdx.Gdx
import com.moandjiezana.toml.Toml

/**
 * Created by Paha on 5/28/2017.
 */
object TOMLTest {
    private val buildingDefName = "data/buildingDefs.toml"

    fun start(){
        val toml = Toml().read(Gdx.files.internal(buildingDefName).file()).to(DefinitionManager.DefList::class.java)
        toml.defs.forEach { def -> DefinitionManager.definitionMap.put(def.name.toLowerCase(), def) }
        println("Such")
    }
}