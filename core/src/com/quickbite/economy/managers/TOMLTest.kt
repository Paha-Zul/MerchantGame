package com.quickbite.economy.managers

import com.badlogic.gdx.Gdx
import com.moandjiezana.toml.Toml

/**
 * Created by Paha on 5/28/2017.
 */
object TOMLTest {
    private val buildingDefName = "data/buildingDefs.toml"
    private val unitDefName = "data/unitDefs.toml"
    private val resourceDefName = "data/resourceDefs.toml"
    private val itemsDefName = "data/itemDefs.toml"
    private val itemProdName = "data/productions.toml"

    fun start(){
        val buildingDefs = Toml().read(Gdx.files.internal(buildingDefName).file()).to(DefinitionManager.DefList::class.java)
        buildingDefs.defs.forEach { def -> DefinitionManager.definitionMap.put(def.name.toLowerCase(), def) }

        val unitDefs = Toml().read(Gdx.files.internal(unitDefName).file()).to(DefinitionManager.DefList::class.java)
        unitDefs.defs.forEach { def -> DefinitionManager.definitionMap.put(def.name.toLowerCase(), def) }

        val resourceDefs = Toml().read(Gdx.files.internal(resourceDefName).file()).to(DefinitionManager.DefList::class.java)
        resourceDefs.defs.forEach { def -> DefinitionManager.definitionMap.put(def.name.toLowerCase(), def) }

        val itemDefList = Toml().read(Gdx.files.internal(itemsDefName).file()).to(ItemDefList::class.java)
        itemDefList.items.forEach { itemDef ->
            DefinitionManager.itemDefMap.put(itemDef.itemName, itemDef)
            itemDef.categories.forEach { DefinitionManager.itemCategoryMap.computeIfAbsent(it, {com.badlogic.gdx.utils.Array()}).add(itemDef) }
        }

        val itemProdList = Toml().read(Gdx.files.internal(itemProdName).file()).to(DefinitionManager.ProductionList::class.java)
        itemProdList.productions.forEach { prod -> DefinitionManager.productionMap.put(prod.producedItem, prod)}
    }

    class ItemDefList{
        var items:Array<DefinitionManager.ItemDef> = arrayOf()
    }
}