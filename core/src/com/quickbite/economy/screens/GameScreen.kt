package com.quickbite.economy.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.quickbite.economy.InputHandler
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.DebugDrawComponent
import com.quickbite.economy.components.InitializationComponent
import com.quickbite.economy.components.PreviewComponent
import com.quickbite.economy.components.TransformComponent
import com.quickbite.economy.event.GameEventSystem
import com.quickbite.economy.event.events.ItemSoldEvent
import com.quickbite.economy.gui.GameScreenGUIManager
import com.quickbite.economy.levels.Level1
import com.quickbite.economy.managers.TownManager
import com.quickbite.economy.systems.*
import com.quickbite.economy.tutorialtest.TutorialTest
import com.quickbite.economy.util.*
import com.quickbite.economy.util.Spawner.town


/**
 * Created by Paha on 12/13/2016.
 */
class GameScreen :Screen{
    val gameScreeData = GameScreenData()
    var shadowObject : Pair<Entity, TransformComponent>? = null
    lateinit var inputHandler:InputHandler
    lateinit var gameScreenGUI:GameScreenGUIManager

    var currentlySelectedType  = ""
        set(value) {
            field = value
            loadNewPreview()
        }

    var showGrid = false

    override fun show() {
        gameScreeData.playerMoney = 1000

        //Subscribe to the general item sold event
        GameEventSystem.subscribe<ItemSoldEvent> {
            gameScreeData.playerMoney += it.taxCollected
        }

        gameScreenGUI = GameScreenGUIManager(this)
        TutorialTest.gameScreenGUIManager = gameScreenGUI

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

        MyGame.entityEngine.addSystem(behaviourSystem)
        MyGame.entityEngine.addSystem(movementSystem)
        MyGame.entityEngine.addSystem(renderSystem)
        MyGame.entityEngine.addSystem(debugSystem)
        MyGame.entityEngine.addSystem(gridSystem)
        MyGame.entityEngine.addSystem(workshopSystem)
        MyGame.entityEngine.addSystem(goldTrackingSystem)
        MyGame.entityEngine.addSystem(resourceSystem)

        MyGame.entityEngine.addEntityListener(object:EntityListener{
            override fun entityRemoved(ent: Entity?) {
                val gc = Mappers.grid.get(ent) //The grid component
                val pc = Mappers.preview.get(ent) //The preview component
                if(gc != null && pc == null){ //We need to make sure we have a grid component AND DO NOT HAVE a preview component.
                    if(gc.blockWhenPlaced){
                        val tc = Mappers.transform.get(ent)
                        MyGame.grid.setUnblocked(tc.position.x, tc.position.y, tc.dimensions.x*0.5f, tc.dimensions.y*0.5f)
                    }
                }
            }

            override fun entityAdded(ent: Entity) {
                val init = Mappers.init.get(ent)
                val preview = Mappers.preview.get(ent)
                if(init != null && preview == null){
                    init.initiated = true
                    init.initFuncs.forEach { func -> func() }
                    ent.remove(InitializationComponent::class.java)
                }
            }
        })

        TownManager.init()

        Level1.start()
//        TutorialTest.test()
    }

    override fun pause() {

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun hide() {

    }

    override fun render(delta: Float) {
        val delta = TimeUtil.scaledDeltaTime

        positionBuildingShadow()

        val batch =  MyGame.batch
        batch.projectionMatrix = MyGame.camera.combined

        batch.begin()

        MyGame.entityEngine.update(TimeUtil.scaledDeltaTime)

        batch.end()

        val renderer = MyGame.renderer

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        renderer.projectionMatrix = MyGame.camera.combined

        if(showGrid) {
            debugDrawLine(renderer)
            debugDrawFilled(renderer)
        }

        //Draw the body of all the things. This is not entity specific so it's outside of the entity loop
        if(DebugDrawComponent.GLOBAL_DEBUG_BODY){
            MyGame.box2DDebugRenderer.render(MyGame.world, MyGame.box2dCamera.combined)
        }

        gameScreenGUI.update(delta)
        Spawner.update(delta)

        Gdx.gl.glDisable(GL20.GL_BLEND)

        TimeOfDay.update(delta)
        updateTown(delta)
    }

    private fun updateTown(delta:Float){
        town.update(delta)
    }

    fun debugDrawLine(renderer:ShapeRenderer){
        renderer.begin(ShapeRenderer.ShapeType.Line)

        MyGame.grid.debugDrawGrid(renderer)

        renderer.end()
    }

    fun debugDrawFilled(renderer:ShapeRenderer){
        renderer.begin(ShapeRenderer.ShapeType.Filled)

        MyGame.grid.debugDrawObstacles(renderer)

        renderer.end()
    }

    private fun positionBuildingShadow(){
        val worldCoords = MyGame.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

        val pos = Vector2(Util.roundDown(worldCoords.x + MyGame.grid.squareSize*0.5f, MyGame.grid.squareSize).toFloat(),
                Util.roundDown(worldCoords.y + MyGame.grid.squareSize*0.5f, MyGame.grid.squareSize).toFloat())

//        System.out.println("pos:$pos, pos2:$pos2")

        shadowObject?.second?.position?.set(pos.x, pos.y )
    }

    fun loadNewPreview(){
        //Remove the last object
        if(shadowObject != null){
            Factory.destroyEntity(shadowObject!!.first)
            shadowObject = null
        }

        //If the new selected type is empty, don't load a new preview
        if(currentlySelectedType.isEmpty())
            return

        //Create the Entity with the preview component
        val worldCoords = MyGame.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

        val pos = Vector2(((worldCoords.x)/MyGame.grid.squareSize).toInt()*MyGame.grid.squareSize.toFloat(),
                ((worldCoords.y)/MyGame.grid.squareSize).toInt()*MyGame.grid.squareSize.toFloat())

        val newEntity = Factory.createObjectFromJson(currentlySelectedType, Vector2(pos), listOf(PreviewComponent()))
        shadowObject = Pair(newEntity!!, Mappers.transform.get(newEntity))
    }

    override fun resume() {

    }

    override fun dispose() {

    }
}
