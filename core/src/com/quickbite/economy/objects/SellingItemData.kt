package com.quickbite.economy.objects

/**
 * Created by Paha on 5/1/2017.
 */
data class SellingItemData(val itemName:String, val itemPrice:Int, var itemStockAmount:Int, val itemSourceType:ItemSource, var itemSourceData:Any? = null){
    enum class ItemSource {None, Import, Workshop, Myself}
}