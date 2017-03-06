package com.quickbite.economy.util

import com.badlogic.gdx.math.MathUtils

/**
 * Created by Paha on 2/1/2017.
 */
object Names {
    val names = listOf("Carylon","Sandie","Nena","Teodoro","Ruth","Norah","Arminda","Julie","Effie","Delbert","Alita","Marya","Ladawn","Arletha","Yuri","Reginia","Thanh","Tashia","Shelba","Andrea")

    val randomName:String
        get() = names[MathUtils.random(names.size-1)]
}