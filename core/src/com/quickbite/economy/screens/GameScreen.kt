package com.quickbite.economy.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.quickbite.economy.CheckHoverOverEntity
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.DebugDrawComponent
import com.quickbite.economy.components.InitializationComponent
import com.quickbite.economy.components.PreviewComponent
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ItemSoldEvent
import com.quickbite.economy.gui.GameScreenGUIManager
import com.quickbite.economy.input.InputHandler
import com.quickbite.economy.levels.LevelManager
import com.quickbite.economy.managers.DefinitionManager
import com.quickbite.economy.managers.TownManager
import com.quickbite.economy.objects.Terrain
import com.quickbite.economy.objects.Town
import com.quickbite.economy.systems.*
import com.quickbite.economy.util.*
import com.quickbite.economy.util.objects.ShadowObject
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.atomic.AtomicInteger


/**
 * Created by Paha on 12/13/2016.
 *
 * The main game screen for a level.
 */
class GameScreen :Screen{
    companion object {
        var showGrid = false
    }

    val gameScreeData = GameScreenData()
    val shadowObject:ShadowObject = ShadowObject()
    lateinit var inputHandler: InputHandler

    var currentlySelectedType  = ""
        set(value) {
            field = value
            loadNewPreview()
        }

    private val myTown: Town by lazy { TownManager.getTown("Town") }

    override fun show() {
        inputHandler = InputHandler(this)
        Gdx.input.inputProcessor = InputMultiplexer(MyGame.stage, inputHandler)

        val behaviourSystem = BehaviourSystem()
        val renderSystem = RenderSystem(MyGame.batch)
        val debugSystem = DebugDrawSystem(MyGame.batch)
        val movementSystem = MovementSystem()
        val gridSystem = GridSystem()
        val workshopSystem = WorkforceSystem(1f)
        val goldTrackingSystem = GoldTrackingSystem()
        val resourceSystem = ResourceSystem(1f)
        val farmSystem = FarmSystem(1f)

        MyGame.entityEngine.addSystem(behaviourSystem)
        MyGame.entityEngine.addSystem(movementSystem)
        MyGame.entityEngine.addSystem(renderSystem)
        MyGame.entityEngine.addSystem(debugSystem)
        MyGame.entityEngine.addSystem(gridSystem)
        MyGame.entityEngine.addSystem(workshopSystem)
        MyGame.entityEngine.addSystem(goldTrackingSystem)
        MyGame.entityEngine.addSystem(resourceSystem)
        MyGame.entityEngine.addSystem(farmSystem)

        //This listens for when an entity is removed. This clears the blocked area of the entity
        MyGame.entityEngine.addEntityListener(object : EntityListener {
            override fun entityRemoved(ent: Entity?) {
                val gc = Mappers.grid.get(ent) //The grid component
                val pc = Mappers.preview.get(ent) //The preview component
                if (gc != null && pc == null) { //We need to make sure we have a grid component AND DO NOT HAVE a preview component.
                    if (gc.blockWhenPlaced) {
                        val tc = Mappers.transform.get(ent)
                        MyGame.grid.setUnblocked(tc.position.x, tc.position.y, tc.dimensions.x * 0.5f, tc.dimensions.y * 0.5f)
                    }
                }
            }

            override fun entityAdded(ent: Entity) {
                val init = Mappers.init.get(ent)
                val preview = Mappers.preview.get(ent)
                if (init != null && preview == null) {
                    init.initiated = true
                    init.initFuncs.forEach { func -> func() }
                    ent.remove(InitializationComponent::class.java)
                }
            }
        })

        initTerrain()
        LevelManager.loadLevel("level 2")
        GameScreenGUIManager.init(this)
//        TutorialTest.test()
        CheckHoverOverEntity.gameScreen = this

        myTown.money = 500f

        //Subscribe to the general item sold event
        GameEventSystem.subscribe<ItemSoldEvent> {
            myTown.money += it.taxCollected
        }
    }

    private fun initTerrain(){
        MyGame.grid.grid.forEach { it.forEach { gridNode ->
            gridNode.terrain = Terrain(TextureRegion(MyGame.manager["grass", Texture::class.java], MyGame.grid.squareSize, MyGame.grid.squareSize),
                    gridNode.xPos, gridNode.yPos)
        } }
    }


    override fun pause() {
//        TimeUtil.paused = !TimeUtil.paused
    }

    override fun resize(width: Int, height: Int) {

    }

    override fun hide() {

    }

    override fun render(delta: Float) {
        val delta = TimeUtil.scaledDeltaTime

        CheckHoverOverEntity.update(delta)

        positionBuildingShadow()

        val batch =  MyGame.batch
        batch.projectionMatrix = MyGame.camera.combined

        drawGame(batch)

        val renderer = MyGame.renderer

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        renderer.projectionMatrix = MyGame.camera.combined

        if(showGrid) {
            debugDrawLine(renderer)
            debugDrawFilled(renderer)
            debugDrawPathfinding(batch)
        }

        //Draw the body of all the things. This is not entity specific so it's outside of the entity loop
        if(DebugDrawComponent.GLOBAL_DEBUG_BODY){
            MyGame.box2DDebugRenderer.render(MyGame.world, MyGame.box2dCamera.combined)
        }

        GameScreenGUIManager.update(delta)
        Spawner.update(delta)

        Gdx.gl.glDisable(GL20.GL_BLEND)

        TimeOfDay.update(delta)
        updateTown(delta)

        checkCameraMove()
    }

    private fun drawGame(batch:SpriteBatch){
        batch.begin()
        drawTerrain(batch)
        MyGame.entityEngine.update(TimeUtil.scaledDeltaTime)
        batch.end()
    }

    /**
     * Controls the movement of the camera.
     */
    private fun checkCameraMove(){
        if(Gdx.input.isKeyPressed(Input.Keys.W)) moveCameras(0f, Constants.CAMERA_MOVE_SPEED)
        else if(Gdx.input.isKeyPressed(Input.Keys.S)) moveCameras(0f, -Constants.CAMERA_MOVE_SPEED)
        if(Gdx.input.isKeyPressed(Input.Keys.A)) moveCameras(-Constants.CAMERA_MOVE_SPEED, 0f)
        else if(Gdx.input.isKeyPressed(Input.Keys.D)) moveCameras(Constants.CAMERA_MOVE_SPEED, 0f)
    }

    private fun moveCameras(x:Float, y:Float){
        MyGame.camera.translate(x, y)
        MyGame.box2dCamera.position.set(MyGame.camera.position.x*Constants.BOX2D_SCALE, MyGame.camera.position.y*Constants.BOX2D_SCALE, 0f)
    }

    private fun drawTerrain(batch:SpriteBatch){
        MyGame.grid.grid.forEach { it.forEach { gridNode ->
            if(gridNode.terrain!=null){
                val terrain = gridNode.terrain!!
                val batchColor = batch.color
                val newColor = Color(1f, 1f, 1f, 1f).lerp(Color.BLACK, terrain.roadLevel/6f)
                batch.color = newColor
                batch.draw(terrain.texture, terrain.x, terrain.y)
                batch.color = batchColor
            }
        } }
    }

    private fun updateTown(delta:Float){
        TownManager.update(delta)
    }

    private fun debugDrawLine(renderer:ShapeRenderer){
        renderer.begin(ShapeRenderer.ShapeType.Line)

        MyGame.grid.debugDrawGrid(renderer)

        renderer.end()
    }

    private fun debugDrawFilled(renderer:ShapeRenderer){
        renderer.begin(ShapeRenderer.ShapeType.Filled)

        MyGame.grid.debugDrawObstacles(renderer)

        renderer.end()
    }

    private fun debugDrawPathfinding(batch:SpriteBatch){
        if(DebugDrawComponent.GLOBAL_DEBUG_PATHFINDING) {
            MyGame.batch.begin()
            MyGame.grid.debugDrawGridScores(batch)
            MyGame.batch.end()
        }
    }

    private fun positionBuildingShadow(){
        if(shadowObject.entity == null)
            return

        val worldCoords = MyGame.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

        val pos = Vector2(Util.roundDown(worldCoords.x + MyGame.grid.squareSize*0.5f, MyGame.grid.squareSize).toFloat(),
                Util.roundDown(worldCoords.y + MyGame.grid.squareSize*0.5f, MyGame.grid.squareSize).toFloat())


        //TODO Need more conditions here for if we can't build
        shadowObject.canBuild = shadowObject.def!!.cost > myTown.money

        if(shadowObject.canBuild)
            shadowObject.graphicComp!!.sprite.color = Color.RED
        else
            shadowObject.graphicComp!!.sprite.color = Color.WHITE

        shadowObject.transformComp?.position?.set(pos.x, pos.y )
    }

    private fun loadNewPreview(){
        //Remove the last object
        if(shadowObject.entity != null){
            Factory.destroyEntity(shadowObject.entity!!)
            shadowObject.entity = null
        }

        //If the new selected type is empty, don't load a new preview
        if(currentlySelectedType.isEmpty())
            return

        //Create the Entity with the preview component
        val worldCoords = MyGame.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

        val pos = Vector2(((worldCoords.x)/MyGame.grid.squareSize).toInt()*MyGame.grid.squareSize.toFloat(),
                ((worldCoords.y)/MyGame.grid.squareSize).toInt()*MyGame.grid.squareSize.toFloat())

        val newEntity = Factory.createObjectFromJson(currentlySelectedType, Vector2(pos), listOf(PreviewComponent()))
        shadowObject.entity = newEntity!!
        shadowObject.transformComp = Mappers.transform.get(newEntity)
        shadowObject.graphicComp = Mappers.graphic[newEntity]
        shadowObject.def = DefinitionManager.constructionDefMap[currentlySelectedType.toLowerCase()]
    }

    override fun resume() {

    }

    override fun dispose() {

    }
}
