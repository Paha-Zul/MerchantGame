package com.quickbite.economy

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

/**
 * Created by Paha on 4/25/2017.
 */

/**
 * A cleaner way to add an action to the ChangeListener's changed function.
 * @param changeListener The function to call inside the ChangeListener's changed function.
 * event - The event...
 * actor - The event target, which is the actor that emitted the change event.
 */
fun Actor.addChangeListener(changeListener: (event: ChangeListener.ChangeEvent, actor: Actor) -> Unit){
    this.addListener(object: ChangeListener(){
        override fun changed(event: ChangeEvent, actor: Actor) {
            changeListener(event, actor)
        }
    })
}

/**
 * Simply checks if this entity has any components attached to it. 0 components means it's been destroyed
 */
fun Entity.isValid():Boolean {
    return this.components.size() > 0
}

fun String.CapitalizeEachToken():String{
    val tokens = this.split(" ").toTypedArray() //Split by the spaces

    //Trim and capitalize the first letter (if not empty)
    for (i in (tokens.size-1).downTo(0)) {
        tokens[i].trim()
        if(tokens[i].isNotBlank()){
            val newToken = tokens[i].toCharArray()
            newToken[0] = newToken[0].toUpperCase()
            tokens[i] = String(newToken)
        }
    }

    return tokens.joinToString(" ")
}
