package com.quickbite.economy.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.quickbite.economy.MyGame
import com.quickbite.economy.components.*
import com.quickbite.economy.util.Mappers
import com.quickbite.economy.util.Util

/**
 * Created by Paha on 1/16/2017.
 */
class DebugDrawSystem(val batch:SpriteBatch) : EntitySystem(){
    lateinit var entities: ImmutableArray<Entity>

    val pixel: TextureRegion = TextureRegion(Util.createPixel(Color.BLACK))
    val centerPixel: TextureRegion = TextureRegion(Util.createPixel(Color.ORANGE))
    val entrancePixel: TextureRegion = TextureRegion(Util.createPixel(Color.GREEN))
    val size = 4f

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)

        entities = engine.getEntitiesFor(Family.all(TransformComponent::class.java, DebugDrawComponent::class.java)
                .one(BehaviourComponent::class.java, BuildingComponent::class.java, ResellingItemsComponent::class.java, BodyComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        entities.forEach { ent ->
            val bm = Mappers.behaviour.get(ent)
            val tm = Mappers.transform.get(ent)
            val dc = Mappers.debugDraw.get(ent)
            val bc = Mappers.building.get(ent)
            val rc = Mappers.reselling.get(ent)
            val bodyComp = Mappers.body.get(ent)

            if((dc.debugDrawPath || DebugDrawComponent.GLOBAL_DEBUG_PATH) && bm != null){
                val path = bm.blackBoard.path
                if(path.isNotEmpty()){
                    for(i in 0..path.size-2){
                        val currPoint = path[i]
                        val nextPoint = path[i+1]

                        val rotation = MathUtils.atan2(nextPoint.y - currPoint.y, nextPoint.x - currPoint.x)* MathUtils.radiansToDegrees
                        val distance = currPoint.dst(nextPoint)
                        this.pixel.setRegion(0f, 0f, distance/ size, 1f)
                        batch.draw(pixel, currPoint.x, currPoint.y, 0f, 0f, distance, size, 1f, 1f, rotation)
                    }
                }
            }

            //Draw the shop link if enabled and we have the reselling component
            if((dc.debugDrawShopLink || DebugDrawComponent.GLOBAL_DEBUG_SHOPLINK) && rc != null){

                rc.resellingItemsList.forEach { link ->
                    val currPoint = tm.position
                    val nextPoint = Mappers.transform.get(link.entity).position

                    val rotation = MathUtils.atan2(nextPoint.y - currPoint.y, nextPoint.x - currPoint.x)* MathUtils.radiansToDegrees
                    val distance = currPoint.dst(nextPoint)
                    this.pixel.setRegion(0f, 0f, distance/ size, 1f)
                    batch.draw(pixel, currPoint.x, currPoint.y, 0f, 0f, distance, size, 1f, 1f, rotation)
                }
            }

            if(dc.debugDrawCenter || DebugDrawComponent.GLOBAL_DEBUG_CENTER)
                batch.draw(centerPixel, tm.position.x - 5f, tm.position.y - 5f, 10f, 10f)

            if(bc != null && (dc.debugDrawEntrace || DebugDrawComponent.GLOBAL_DEBUG_ENTRANCE) && bc.entranceSpotOffsets.size > 0)
                bc.entranceSpotOffsets.forEach { entrance ->
                    batch.draw(entrancePixel, tm.position.x + entrance.x - 5f, tm.position.y + entrance.y - 5f, 10f, 10f)
                }
        }


    }
}