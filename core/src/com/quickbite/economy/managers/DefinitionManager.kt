package com.quickbite.economy.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.moandjiezana.toml.Toml
import com.quickbite.economy.objects.ItemAmountLink
import com.quickbite.economy.objects.ItemPriceLink
import com.quickbite.economy.objects.WorkerTaskLimitLink

/**
 * Created by Paha on 2/16/2017.
 */
object DefinitionManager {
    private val json = Json()
    val definitionMap: HashMap<String, Definition> = hashMapOf()
    val itemDefMap: HashMap<String, ItemDef> = hashMapOf()
    val itemCategoryMap:HashMap<String, com.badlogic.gdx.utils.Array<ItemDef>> = hashMapOf()
    val productionMap: HashMap<String, Production> = hashMapOf()

    lateinit var names:Names

    private val buildingDefName = "data/buildingDefs.toml"
    private val unitDefName = "data/unitDefs.toml"
    private val resourceDefName = "data/resourceDefs.toml"
    private val itemsDefName = "data/itemDefs.toml"
    private val itemProdName = "data/productions.toml"
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

        //Add a serializer for ItemAmountLink
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

        //Add a serializer for WorkerTaskLimitLink
        json.setSerializer(WorkerTaskLimitLink::class.java, object: Json.Serializer<WorkerTaskLimitLink> {
            override fun read(json: Json, jsonData: JsonValue, type: Class<*>): WorkerTaskLimitLink {
                val data = jsonData.child
                val link = WorkerTaskLimitLink(data.asString(), data.next.asInt()) //Make the item link
                return link //Return in
            }

            override fun write(json: Json, `object`: WorkerTaskLimitLink, knownType: Class<*>?) {
                json.writeValue(arrayOf(`object`.taskName, `object`.amount)) //Write as an array
            }
        })
    }

    fun loadDefinitions(){
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

        this.names = json.fromJson(Names::class.java, Gdx.files.internal(namesDefName))

    }

    /**
     * Clears all definitions and reloads them
     */
    fun clearAllDataAndReload(){
        definitionMap.clear()
        itemDefMap.clear()
        itemCategoryMap.clear()
        productionMap.clear()

        loadDefinitions()
    }

    class Names{
        lateinit var firstNames:com.badlogic.gdx.utils.Array<String>
        lateinit var lastNames:com.badlogic.gdx.utils.Array<String>
    }

    class DefList {
        lateinit var defs:Array<Definition>
    }

    class ItemDefList{
        var items:Array<DefinitionManager.ItemDef> = arrayOf()
    }

    class Definition {
        var name = ""
        var identityDef = IdentityDef() //Must exist
        var graphicDef = GraphicDef() //Must exist
        var transformDef = TransformDef() //Must exist
        var gridDef:GridDef = GridDef() //Must exist
        var buildingDef:BuildingDef? = null //Optional
        var velocityDef:VelocityDef? = null //Optional
        var physicsDef:PhysicsDef? = null //Optional
        var inventoryDef:InventoryDef? = null //Optional
        var productionDef:ProductionDef? = null //Optional
        var sellingItems:SellingDef? = null //Optional
        var workforceDef:WorkforceDef? = null //Optional
        var resourceDef:ResourceDef? = null //Optional
        var behaviourDef:BehaviourDef? = null //Optional
        var workerDef:WorkerDef? = null //Optional
        var buyerDef:BuyerDef? = null //Optional
        var compsToAdd:List<ComponentDef> = listOf()
    }

    class IdentityDef{
        var useRandomName:Boolean = false
    }

    class GridDef{
        var onGrid = false
        var blockGridWhenPlaced = false
    }

    class ProductionDef{
        var produces:Array<String> = arrayOf()
        var harvests:Array<String> = arrayOf()
    }

    class BehaviourDef{
    }

    class WorkerDef{
    }

    class BuyerDef{
    }

    class TransformDef{
        var physicalDimensions = Vector2()
        var spots:HashMap<String, Array<Vector2>> = hashMapOf()
    }

    class WorkforceDef{
        var workforceMax = 0
        lateinit var workerTasks:Array<WorkerTaskLimitLink>
    }

    class BuildingDef{
        var isBuilding = false
        var buildingType = ""
    }

    class PhysicsDef{
        var bodyType = ""
        var bodyAnchor = Vector2()
        var bodyDimensions = Vector2()
    }

    class VelocityDef{
        var baseSpeed = 0f
    }

    class InventoryDef{
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
        var initialAnimation = true
    }

    class SellingDef{
        var sellingList:Array<ItemPriceLink> = arrayOf()
        var isReselling = false
        var taxRate = 0f
    }

    class ResourceDef{
        var resourceType:String = ""
        var resourceAmount:Int = 0
        var numHarvestersMax:Int = 0
        var harvestAmount = 0
        var harvestedItemName = ""
        var baseHarvestTime = 0f
        var canRegrow = false
        var baseRegrowTimeRange = arrayOf(180, 300)
    }

    class ItemDef{
        lateinit var itemName:String
        var baseMarketPrice:Int = 0
        var categories:Array<String> = arrayOf()
        var iconName = ""
        var need = 0
        var luxury = 0
        var baseQuality = 1
    }

    class ProductionList{
        lateinit var productions:Array<Production>
    }

    class Production{
        lateinit var producedItem:String
        var produceAmount:Int = 0
        lateinit var requirements:Array<ItemAmountLink>
    }

    class ConstructionDef{
        var cost = 0
        var buildingTimeGameMinutes = 60 //Default of 1 hour
    }
}