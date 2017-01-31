package com.quickbite.economy.components

import com.badlogic.gdx.utils.Array
import com.quickbite.economy.interfaces.MyComponent
import com.quickbite.economy.util.MutablePair

/**
 * Created by Paha on 1/25/2017.
 *
 * A component for units wanting to buy. Maybe should be called 'NeedsComponent' or 'DemandsComponent'?
 */
class BuyerComponent : MyComponent{
    enum class BuyerFlag{
        None, Bought, Failed
    }

    /** A list of pairs containing (item name, item amount) */
    val buyList:Array<MutablePair<String, Int>> = Array(5)
    var moneyAvailable = 0
    var buyerFlag = BuyerFlag.None

    override fun dispose() {

    }

    override fun initialize() {

    }
}