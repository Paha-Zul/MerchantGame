package com.quickbite.economy.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.quickbite.economy.InputHandler
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.PreviewComponent
import com.quickbite.economy.components.TransformComponent
import com.quickbite.economy.systems.*
import com.quickbite.economy.util.Factory
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util


/**
 * Created by Paha on 12/13/2016.
 */
class GameScreen :Screen{
    val dot = Util.createPixel(Color.RED)
    var shadowObject : Pair<Entity, TransformComponent>? = null

    var currentlySelectedType  = ""
        set(value) {
            field = value
            loadNewPreview()
        }

    var showGrid = false

    override fun show() {
        Gdx.input.inputProcessor = InputHandler(this)

        val behaviourSystem = BehaviourSystem()
        val renderSystem = RenderSystem(MyGame.batch)
        val debugSystem = DebugDrawSystem(MyGame.batch)
        val movementSystem = MovementSystem()
        val gridSystem = GridSystem()

        MyGame.entityEngine.addSystem(behaviourSystem)
        MyGame.entityEngine.addSystem(renderSystem)
        MyGame.entityEngine.addSystem(movementSystem)
        MyGame.entityEngine.addSystem(debugSystem)
        MyGame.entityEngine.addSystem(gridSystem)

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

            override fun entityAdded(p0: Entity?) {

            }
        })
    }

    override fun pause() {

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun hide() {

    }

    override fun render(delta: Float) {
        positionBuildingShadow()

        val batch =  MyGame.batch
        batch.projectionMatrix = MyGame.camera.combined

        batch.begin()

        MyGame.entityEngine.update(delta)
        batch.draw(dot, 0f, 0f, 25f, 25f)
        batch.draw(dot, 300f, 0f, 25f, 25f)
        batch.draw(dot, -300f, 0f, 25f, 25f)
//        shadowBuildingPlacement.draw(batch)

        batch.end()

        val renderer = MyGame.renderer

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        renderer.projectionMatrix = MyGame.camera.combined

        if(showGrid) {
            debugDrawLine(renderer)
            debugDrawFilled(renderer)
        }

        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    fun debugDrawLine(renderer:ShapeRenderer){
        renderer.begin(ShapeRenderer.ShapeType.Line)

        MyGame.grid.debugDraw(renderer)

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
            MyGame.entityEngine.removeEntity(shadowObject!!.first)
            shadowObject = null
        }

        //If the new selected type is empty, don't load a new preview
        if(currentlySelectedType.isEmpty())
            return

        //Create the Entity with the preview component
        val worldCoords = MyGame.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

        val pos = Vector2(((worldCoords.x)/MyGame.grid.squareSize).toInt()*MyGame.grid.squareSize.toFloat(),
                ((worldCoords.y)/MyGame.grid.squareSize).toInt()*MyGame.grid.squareSize.toFloat())

        val newEntity = Factory.createObject(currentlySelectedType, Vector2(pos), Vector2(0f,0f), "Building", listOf(PreviewComponent()))
        shadowObject = Pair(newEntity!!, Mappers.transform.get(newEntity))
    }

    override fun resume() {

    }

    override fun dispose() {

    }
}
