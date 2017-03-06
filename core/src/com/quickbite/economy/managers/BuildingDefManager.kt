package com.quickbite.economy.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quickbite.economy.util.ItemPriceLink
import java.util.*

/**
 * Created by Paha on 2/16/2017.
 */
object BuildingDefManager {
    val json = Json()
    val buildingDefsMap: HashMap<String, BuildingDefinition> = hashMapOf()
    val buildingsFileName = "data/buildings.json"

    init {
        json.setSerializer(ItemPriceLink::class.java, object: Json.Serializer<ItemPriceLink> {
            override fun read(json: Json, jsonData: JsonValue, type: Class<*>): ItemPriceLink {
                val data = jsonData.child
                val itemAmountLink = ItemPriceLink(data.asString(), data.next.asInt()) //Make the item link
                return itemAmountLink //Return in
            }

            override fun write(json: Json, `object`: ItemPriceLink, knownType: Class<*>?) {
                json.writeValue(arrayOf(`object`.itemName, `object`.itemPrice)) //Write as an array
            }

        })
    }

    fun readBuildingDefJson(){
        val list = json.fromJson(BuildingDefList::class.java, Gdx.files.internal(buildingsFileName))
        list.buildingDefs.forEach { buildingDef -> buildingDefsMap.put(buildingDef.name.toLowerCase(), buildingDef)}
    }

    private class BuildingDefList{
        lateinit var buildingDefs:Array<BuildingDefinition>
    }

    class BuildingDefinition {
        var name = ""
        var graphic = ""
        var graphicAnchor:Array<Float> = arrayOf()
        var graphicInitialAnimation = false
        var buildingType = ""
        var graphicSize:Array<Float> = arrayOf()
        var physicalDimensions:Array<Float> = arrayOf()
        var gridBlockWhenPlaced = true
        var hasInventory = false
        var sellingItems:Array<ItemPriceLink> = arrayOf()
        var workforceMax = 0
        var workerTasks:com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.Array<String>> = com.badlogic.gdx.utils.Array()
        var reselling = false
    }
}