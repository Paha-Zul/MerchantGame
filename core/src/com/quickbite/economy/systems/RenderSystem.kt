package com.quickbite.economy.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.quickbite.economy.components.GraphicComponent
import com.quickbite.economy.components.TransformComponent
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 1/16/2017.
 * An EntitySystem that handles drawing graphics
 */
class RenderSystem(val batch:SpriteBatch) : EntitySystem(){
    lateinit var entities: ImmutableArray<Entity>

    val bounceOut = Interpolation.bounceOut

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)

        entities = engine.getEntitiesFor(Family.all(GraphicComponent::class.java, TransformComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        entities.forEach { ent ->
            val gc = Mappers.graphic.get(ent)
            val tc = Mappers.transform.get(ent)
            val pc = Mappers.preview.get(ent)

            if(pc != null)
                gc.sprite.setAlpha(0.2f)

            gc.sprite.setPosition(tc.position.x - gc.anchor.x*tc.dimensions.x, tc.position.y - gc.anchor.y*tc.dimensions.y)

            if(gc.initialAnimation && pc == null){
                runAnimation(deltaTime, gc, tc)
            }

            if(!gc.hide)
                gc.sprite.draw(batch)
        }
    }

    private fun runAnimation(delta:Float, gc:GraphicComponent, tc:TransformComponent){
        val pos = Vector2(tc.position.x, tc.position.y + 25)
        val out = bounceOut.apply(pos.y, tc.position.y, gc.animationCounter)

        gc.animationCounter += delta
        gc.sprite.setPosition(tc.position.x - gc.anchor.x*tc.dimensions.x, out - gc.anchor.y*tc.dimensions.y)

        if(gc.animationCounter >= 1f)
            gc.initialAnimation = false
    }
}