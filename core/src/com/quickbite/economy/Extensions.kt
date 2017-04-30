package com.quickbite.economy

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