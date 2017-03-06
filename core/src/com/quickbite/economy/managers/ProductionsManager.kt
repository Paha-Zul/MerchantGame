package com.quickbite.economy.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quickbite.economy.util.ItemAmountLink
import java.util.*

/**
 * Created by Paha on 1/31/2017.
 */
object ProductionsManager {
    val json = Json()
    val productionMap: HashMap<String, Production> = hashMapOf()

    init {
        json.setSerializer(ItemAmountLink::class.java, object: Json.Serializer<ItemAmountLink> {
            override fun read(json: Json, jsonData: JsonValue, type: Class<*>): ItemAmountLink {
                val data = jsonData.child
                val itemAmountLink = ItemAmountLink(data.asString(), data.next.asInt()) //Make the item link
                return itemAmountLink //Return in
            }

            override fun write(json: Json, `object`: ItemAmountLink, knownType: Class<*>?) {
                json.writeValue(arrayOf(`object`.itemName, `object`.itemAmount)) //Write as an array
            }

        })
    }

    fun readProductionJson(){
        val list = json.fromJson(ProductionList::class.java, Gdx.files.internal("data/production.json"))
        list.productions.forEach { prod -> productionMap.put(prod.producedItem, prod)}
    }

    private class ProductionList{
        lateinit var productions:Array<Production>
    }

    class Production{
        lateinit var producedItem:String
        var produceAmount:Int = 0
        lateinit var requirements:Array<ItemAmountLink>
    }
}