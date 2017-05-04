package com.quickbite.economy.objects

/**
 * Created by Paha on 5/1/2017.
 */
data class SellingItemData(val itemName:String, val itemPrice:Int, var itemStockAmount:Int, var itemSourceData:Any? = null){
    enum class ItemSource {None, Import, Workshop}
    var itemSourceType:ItemSource = ItemSource.None

    init{
        if(itemSourceData != null)
            itemSourceType = ItemSource.Workshop
    }
}