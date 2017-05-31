package com.quickbite.economy.components

/**
 * Created by Paha on 5/29/2017.
 *
 * A state machine or something
 */
class StateComponent {
    class StateNode(val name:String){
        private val paths:MutableList<StateNode> = mutableListOf()
        fun addPath(name:String):StateNode{
            val node = StateNode(name)
            paths.add(node)
            return node
        }
        fun getPath(name:String):StateNode? = paths.firstOrNull{it.name == name}
    }

    lateinit var currState:StateNode

    init{
        currState = StateNode("Idle")
        val hauling = currState.addPath("Hauling")
        val producing = currState.addPath("Producing")
        val selling = currState.addPath("Selling")
    }
}