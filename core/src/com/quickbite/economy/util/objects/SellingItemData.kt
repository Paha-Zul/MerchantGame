package com.quickbite.economy.util.objects

/**
 * Created by Paha on 5/1/2017.
 * A class to hold data for an item being sold
 * @param itemName The name of the item
 * @param itemPrice The price that the item is selling for
 * @param itemStockAmount The amount the item should be stocked to (also includes producing up to the stock amount)
 * @param itemSourceType The type of source
 * @param itemSourceData Optional data to include. For instance, if the source type is Workshop, the data will probably be the
 * workshop Entity that the item is being imported from
 */
data class SellingItemData(val itemName:String, val itemPrice:Int, var itemStockAmount:Int,
                           val itemSourceType: ItemSource, var itemSourceData:Any? = null){
    enum class ItemSource {None, Import, Workshop, Myself}
}