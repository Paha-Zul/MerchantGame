package com.quickbite.economy.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quickbite.economy.util.ItemAmountLink
import java.util.*

/**
 * Created by Paha on 3/16/2017.
 */
object ItemDefManager {
    val json = Json()
    val itemDefMap: HashMap<String, ItemDef> = hashMapOf()

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

    fun readDefinitionJson(){
        val list = json.fromJson(Array<ItemDef>::class.java, Gdx.files.internal("data/itemDefs.json"))
        list.forEach { itemDef -> itemDefMap.put(itemDef.itemName, itemDef)}
    }

    class ItemDef{
        lateinit var itemName:String
        var baseMarketPrice:Int = 0
    }
}