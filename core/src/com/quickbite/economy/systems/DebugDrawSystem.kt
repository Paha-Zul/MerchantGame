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
import com.badlogic.gdx.math.Vector2
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
                .one(BehaviourComponent::class.java, BuildingComponent::class.java, SellingItemsComponent::class.java, BodyComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        entities.forEach { ent ->
            val bm = Mappers.behaviour.get(ent)
            val tc = Mappers.transform.get(ent)
            val dc = Mappers.debugDraw.get(ent)
            val bc = Mappers.building.get(ent)
            val sc = Mappers.selling.get(ent)
            val wc = Mappers.workforce.get(ent)
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
            if((dc.debugDrawShopLink || DebugDrawComponent.GLOBAL_DEBUG_SHOPLINK) && sc != null){

                sc.resellingItemsList.forEach { (_, _, _, _, entity) ->
                    val entity = entity as? Entity
                    if(entity != null) {
                        val currPoint = tc.position
                        val nextPoint = Mappers.transform.get(entity).position

                        drawLineTo(currPoint, nextPoint)
                    }
                }
            }

            if(dc.debugDrawCenter || DebugDrawComponent.GLOBAL_DEBUG_CENTER)
                batch.draw(centerPixel, tc.position.x - 5f, tc.position.y - 5f, 10f, 10f)

            //TODO Need to handle all spots?
            if(tc != null && (dc.debugDrawEntrace || DebugDrawComponent.GLOBAL_DEBUG_ENTRANCE) && tc.spotMap.isNotEmpty()){
                val values = tc.spotMap.values //Get the values
                values.forEach { list -> //For each list
                    list.forEach { point -> //For each point
                        batch.draw(entrancePixel, tc.position.x + point.x - 5f, tc.position.y + point.y - 5f, 10f, 10f)
                    }
                }
            }

            if(wc != null && dc.debugDrawWorkers){
                wc.workersAvailable.forEach { entity ->
                    val workerPosition = Mappers.transform[entity].position
                    drawLineTo(workerPosition, tc.position)
                }
            }
        }
    }

    private fun drawLineTo(start: Vector2, end:Vector2){
        val rotation = MathUtils.atan2(end.y - start.y, end.x - start.x)* MathUtils.radiansToDegrees
        val distance = start.dst(end)
        this.pixel.setRegion(0f, 0f, distance/ size, 1f)
        batch.draw(pixel, start.x, start.y, 0f, 0f, distance, size, 1f, 1f, rotation)
    }
}