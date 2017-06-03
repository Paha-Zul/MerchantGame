package com.quickbite.economy.filters

/**
 * Created by Paha on 6/2/2017.
 */
class ResourceParameter {
    companion object{
        enum class ResourceQuality{
            Bad, Normal, Good
        }
    }

    /** Used to get a general type of resource, ie: wood */
    var resourceType = ""
    /** A Hash set used to indicate multiple resources, ie: any tree that gives a 'Wood Log' or 'Good Wood Log', etc... */
    var harvestedItemNames = hashSetOf<String>()
    /** Used to get resources of a certain quality */
    var resourceQuality = ResourceQuality.Normal
}