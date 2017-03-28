package com.quickbite.economy.behaviour.leaf

import com.quickbite.economy.behaviour.BlackBoard
import com.quickbite.economy.behaviour.LeafTask
import com.quickbite.economy.managers.TownManager
import com.quickbite.economy.util.Mappers

/**
 * Created by Paha on 3/25/2017.
 */
class ApplyRatingsToTown(bb:BlackBoard) : LeafTask(bb) {
    override fun start() {
        super.start()

        val buyer = Mappers.buyer[bb.myself]
        val town = TownManager.getTown("Town")

        if(buyer != null) {
            town.needsRating += buyer.needsSatisfactionRating
            town.luxuryRating += buyer.luxurySatisfactionRating

            System.out.println("Town n:${town.needsRating}, l: ${town.luxuryRating}")
        }

        controller.finishWithSuccess()
    }
}