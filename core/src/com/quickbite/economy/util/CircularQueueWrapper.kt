package com.quickbite.economy.util

import java.util.*


/**
 * Created by Paha on 4/29/2017.
 * Simply wraps a Queue<T> and provides a single add() function that will
 * remove the first element of the queue if the maxSize is reached
 */
class CircularQueueWrapper<T>(val maxSize:Int) {
    val queue:LinkedList<T> = LinkedList()

    fun add(element:T){
        if(queue.size + 1 > maxSize)
            queue.removeFirst()

        queue.add(element)
    }

    operator fun plusAssign(element:T){
        add(element)
    }
}