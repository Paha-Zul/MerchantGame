package com.quickbite.economy.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quickbite.economy.util.ItemAmountLink
import com.quickbite.economy.util.ItemPriceLink
import java.util.*

/**
 * Created by Paha on 2/16/2017.
 */
object DefinitionManager {
    val json = Json()
    val definitionMap: HashMap<String, Definition> = hashMapOf()
    lateinit var names:Names

    private val buildingDefName = "data/buildingDefs.json"
    private val unitDefName = "data/unitDefs.json"
    private val namesDefName = "data/names.json"

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

        json.setSerializer(Vector2::class.java, object: Json.Serializer<Vector2> {
            override fun read(json: Json, jsonData: JsonValue, type: Class<*>): Vector2 {
                val data = jsonData.child
                val vector = Vector2(data.asFloat(), data.next.asFloat()) //Make the item link
                return vector //Return in
            }

            override fun write(json: Json, `object`: Vector2, knownType: Class<*>?) {
                json.writeValue(arrayOf(`object`.x, `object`.y)) //Write as an array
            }

        })
    }

    fun readDefinitionsJson(){
        //Load the building defs
        var list = json.fromJson(DefList::class.java, Gdx.files.internal(buildingDefName))
        list.buildingDefs.forEach { def -> definitionMap.put(def.name.toLowerCase(), def)}

        //Load the unit defs
        list = json.fromJson(DefList::class.java, Gdx.files.internal(unitDefName))
        list.buildingDefs.forEach { def -> definitionMap.put(def.name.toLowerCase(), def)}

        this.names = json.fromJson(Names::class.java, Gdx.files.internal(namesDefName))
    }

    class Names{
        lateinit var firstNames:com.badlogic.gdx.utils.Array<String>
        lateinit var lastNames:com.badlogic.gdx.utils.Array<String>
    }

    private class DefList {
        lateinit var buildingDefs:Array<Definition>
    }

    class Definition {
        var name = ""
        var identityDef = IdentityDef()
        var graphicDef = GraphicDef()
        var buildingDef = BuildingDef()
        var physicalDimensions:Array<Float> = arrayOf()
        var hasBehaviours = false
        var velocityDef = VelocityDef()
        var physicsDef:PhysicsDef = PhysicsDef()
        var isBuyer = false
        var isWorker = false
        var inventoryDef = InventoryDef()
        var productionDef = ProductionDef()
        var onGrid = false
        var gridBlockWhenPlaced = false
        var sellingItems:SellingDef = SellingDef()
        var workforceDef:WorkforceDef = WorkforceDef()
        var compsToAdd:List<ComponentDef> = listOf()
    }

    class IdentityDef{
        var useRandomName:Boolean = false
    }

    class ProductionDef{
        var produces:com.badlogic.gdx.utils.Array<String> = com.badlogic.gdx.utils.Array()
    }

    class WorkforceDef{
        var workforceMax = 0
        var workerTasks:com.badlogic.gdx.utils.Array<String> = com.badlogic.gdx.utils.Array()
    }

    class BuildingDef{
        var isBuilding = false
        var buildingType = ""
        var entranceSpots:com.badlogic.gdx.utils.Array<Vector2> = com.badlogic.gdx.utils.Array()
    }

    class PhysicsDef{
        var hasPhysicsBody = false
        var bodyType = ""
    }

    class VelocityDef{
        var hasVelocity = false
        var baseSpeed = 0f
    }

    class InventoryDef{
        var hasInventory = false
        var debugItemList:Array<ItemAmountLink> = arrayOf()
    }

    class ComponentDef{
        var compName = ""
        var fields:Array<Array<String>> = arrayOf()
    }

    class GraphicDef{
        var graphicName = ""
        var graphicAnchor:Array<Float> = arrayOf()
        var graphicSize:Array<Float> = arrayOf()
        var initialAnimation = false
    }

    class SellingDef{
        var isSelling = false
        var sellingList:com.badlogic.gdx.utils.Array<ItemPriceLink> = com.badlogic.gdx.utils.Array()
        var isReselling = false
        var taxRate = 0f
    }
}