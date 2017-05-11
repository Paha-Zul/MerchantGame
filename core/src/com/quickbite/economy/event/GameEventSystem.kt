package com.quickbite.economy.event

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Paha on 4/26/2017.
 * A new form of event system...
 */
object GameEventSystem {
    class GameEventContext{
        internal var unsubscribe = false
        fun unsubscribe() {
            unsubscribe = true
        }
    }

    class GameEventRegistration(val eventType: KClass<out GameEvent>, val action: GameEventContext.(GameEvent) -> Unit, val ID:Long){
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as GameEventRegistration

            if (action != other.action) return false

            return true
        }

        override fun hashCode(): Int {
            return action.hashCode()
        }
    }


//    val subscriptions: HashMap<KClass<out GameEvent>, ArrayList<GameEventRegistration>> = hashMapOf()

    val subscriptions: HashMap<KClass<out GameEvent>, Long2ObjectOpenHashMap<ArrayList<GameEventRegistration>>> = hashMapOf()

    /***
     * Subscribes a GameEvent to this system
     * @param action The function to invoke when the event is fired.
     * @param ID A Long ID to subscribe this event to. -1 is default and signifies global
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T:GameEvent> subscribe(noinline action: GameEventContext.(T) -> Unit, ID:Long) : GameEventRegistration{
        val registration = GameEventRegistration(T::class, action as GameEventContext.(GameEvent) -> Unit, ID)
        subscriptions.computeIfAbsent(T::class, {Long2ObjectOpenHashMap(10)}).computeIfAbsent(ID, {ArrayList()}).add(registration)
//        subscriptions.computeIfAbsent(T::class, {ArrayList()}).add(registration)
        return registration
    }

    /**
     * Subscribes a GameEvent to this system. Uses -1 as the ID and is considered a global scoped event
     * @param action The function to invoke when the event is fired.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T:GameEvent> subscribe(noinline action: GameEventContext.(T) -> Unit) : GameEventRegistration{
        val registration = GameEventRegistration(T::class, action as GameEventContext.(GameEvent) -> Unit, -1)
        subscriptions.computeIfAbsent(T::class, {Long2ObjectOpenHashMap(10)}).computeIfAbsent(-1, {ArrayList()}).add(registration)
//        subscriptions.computeIfAbsent(T::class, {ArrayList()}).add(registration)
        return registration
    }

    /**
     * Fires a GameEvent in this system
     * @param event The GameEvent class to fire.
     * @param ID The ID used for specific entities. -1 is default and signifies global.
     */
    fun <T:GameEvent> fire(event:T, ID: Long = -1){
        subscriptions[event.javaClass.kotlin]?.get(ID)?.forEach{
            val context = GameEventContext()
            it.action.invoke(context, event)
            //TODO Unsubscribe from here if done?
        }
    }

    /**
     * Unsubscribes an event from this system. The ID must be specified here.
     * @param action The action to remove
     * @param ID The Long ID of the action
     */
    @Suppress("UNCHECKED_CAST")
    fun unsubscribe(registration: GameEventRegistration) {
        subscriptions[registration.eventType]?.get(registration.ID)?.removeAll { it.action == registration.action }
    }

    /**
     * Unsubscribes all events from the event system for the ID specified
     */
    fun unsubscribeAll(ID:Long){
        subscriptions.values.forEach { it.remove(ID) }
    }
}