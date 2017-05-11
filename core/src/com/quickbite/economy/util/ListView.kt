package com.quickbite.economy.util

/**
 * Created by Paha on 5/10/2017.
 * Extremely simply wrapper around a List value that only allows reading of the list.
 */
class ListView<out T>(private val list:List<T>) {
    val size:Int = list.size

    fun get(index:Int):T{
        return list[index]
    }
}