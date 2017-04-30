package com.quickbite.economy.util

import com.badlogic.gdx.utils.Queue

/**
 * Created by Paha on 4/29/2017.
 * Simply wraps a Queue<T> and provides a single add() function that will
 * remove the first element of the queue if the maxSize is reached
 */
class CircularQueueWrapper<T>(val maxSize:Int) {
    val queue:Queue<T> = Queue()

    fun add(element:T){
        if(queue.size + 1 > maxSize)
            queue.removeFirst()

        queue.addLast(element)
    }

    operator fun plusAssign(element:T){
        add(element)
    }
}