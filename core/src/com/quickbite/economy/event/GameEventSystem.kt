package com.quickbite.economy.event

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

    class GameEventRegistration(val eventType: KClass<out GameEvent>, val action: GameEventContext.(GameEvent) -> Unit){
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

    val subscriptions: HashMap<KClass<out GameEvent>, ArrayList<GameEventRegistration>> = hashMapOf()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T:GameEvent> subscribe(noinline action: GameEventContext.(T) -> Unit) : GameEventContext.(T) -> Unit{
        val registration = GameEventRegistration(T::class, action as GameEventContext.(GameEvent) -> Unit)
        subscriptions.computeIfAbsent(T::class, {ArrayList()}).add(registration)
        return action
    }

    fun <T:GameEvent> fire(event:T){
        subscriptions[event.javaClass.kotlin]?.forEach{
            val context = GameEventContext()
            it.action.invoke(context, event)
            //TODO Unsubscribe from here if done?
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : GameEvent> unsubscribe(noinline action: GameEventContext.(T) -> Unit) {
        subscriptions[T::class]?.removeAll { it.action == action }
    }
}