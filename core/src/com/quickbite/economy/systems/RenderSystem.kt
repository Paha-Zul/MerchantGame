package com.quickbite.economy.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.GraphicComponent
import com.quickbite.economy.components.TransformComponent
import com.quickbite.economy.gui.GameScreenGUIManager
import com.quickbite.economy.util.Mappers
import java.util.*

/**
 * Created by Paha on 1/16/2017.
 * An EntitySystem that handles drawing graphics
 */
class RenderSystem(val batch:SpriteBatch) : EntitySystem(){
    class LinkToArrow{
        var active = false
        val start = Vector2()
    }

    companion object {
        private val linkArrow:NinePatchDrawable
        var linkToArrow = LinkToArrow()

        init{
            val arrow = NinePatch(MyGame.manager["arrow_back", Texture::class.java], 20, 5, 10, 10)
            linkArrow = NinePatchDrawable(arrow)
        }
    }

    lateinit var entities: ImmutableArray<Entity>
    lateinit var sortedEntities:List<Entity>

    val bounceOut: Interpolation.BounceOut = Interpolation.bounceOut

    init{

    }

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)

        val family = Family.all(GraphicComponent::class.java, TransformComponent::class.java).get()
        entities = engine.getEntitiesFor(family)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        //TODO This could possibly get super laggy. Might need a better way to deal with this...
        sortedEntities = entities.sortedWith(Comparator<Entity> { o1, o2 ->
            val tc1 = Mappers.transform[o1]
            val tc2 = Mappers.transform[o2]
            when {
                tc2.position.y - tc1.position.y < 0 -> -1
                tc2.position.y - tc1.position.y > 0 -> 1
                else -> 0
            }
        })

        sortedEntities.forEach { ent ->
            val gc = Mappers.graphic.get(ent)
            val tc = Mappers.transform.get(ent)
            val pc = Mappers.preview.get(ent)
            val fc = Mappers.farm[ent]

            if(pc != null)
                gc.sprite.setAlpha(0.2f)

            gc.sprite.setPosition(tc.position.x - gc.anchor.x*gc.sprite.width, tc.position.y - gc.anchor.y*gc.sprite.height)

            if(gc.initialAnimation && pc == null){
                runAnimation(deltaTime, gc, tc)
            }

            if(!gc.hidden)
                gc.sprite.draw(batch)

            if(fc != null){
                val xSpots = fc.plantSpots.size - 1
                val ySpots = fc.plantSpots[0].size - 1

                //Need to iterate backwards to draw in the correct order
                for(y in ySpots.downTo(0)){
                    for(x in xSpots.downTo(0)){
                        val spot = fc.plantSpots[x][y]
                        spot.sprite.setPosition(spot.position.x + tc.position.x - spot.sprite.width/2f, spot.position.y + tc.position.y)
                        spot.sprite.draw(batch)
                    }
                }
            }
        }

        if(linkToArrow.active)
            drawLinkArrow(linkToArrow.start)
    }

    private fun runAnimation(delta:Float, gc:GraphicComponent, tc:TransformComponent){
        val pos = Vector2(tc.position.x, tc.position.y + 25)
        val out = bounceOut.apply(pos.y, tc.position.y, gc.animationCounter)

        gc.animationCounter += delta
        gc.sprite.setPosition(tc.position.x - gc.anchor.x*gc.sprite.width, out - gc.anchor.y*gc.sprite.height)

        if(gc.animationCounter >= 1f)
            gc.initialAnimation = false
    }


    fun drawLinkArrow(start:Vector2){
        val endWorldCoords = MyGame.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 1f))

        drawLineTo(start,
                Vector2(endWorldCoords.x, endWorldCoords.y), linkArrow)
    }

    private fun drawLineTo(start: Vector2, end:Vector2, texture: NinePatchDrawable){
        val rotation = MathUtils.atan2(end.y - start.y, end.x - start.x).toDouble()
        val distance = start.dst(end)
        texture.draw(MyGame.batch, start.x, start.y, 0f, 16f, distance,
                32f, 1f, 1f, rotation.toFloat()* MathUtils.radiansToDegrees)
    }
}