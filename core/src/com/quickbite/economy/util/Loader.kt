package com.quickbite.economy.util

import com.badlogic.gdx.assets.loaders.BitmapFontLoader
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas

/**
 * Created by Paha on 6/19/2016.
 */
object Loader {
    fun loadAllImgs(manager: EasyAssetManager, dir: FileHandle, recursive:Boolean = false){
        val params = TextureLoader.TextureParameter()
        params.genMipMaps = true
        params.minFilter = Texture.TextureFilter.MipMapLinearLinear
        params.magFilter = Texture.TextureFilter.MipMapLinearLinear

        for(file in dir.list()){
            if(file.isDirectory && recursive)
                loadAllImgs(manager, file, true)
            else if(file.extension() == "png"){
                manager.load(file.path(), Texture::class.java, params)
            }
        }
    }

    fun loadAtlas(manager: EasyAssetManager, dir: FileHandle, recursive:Boolean = false){
        for(file in dir.list()){
            if(file.isDirectory && recursive)
                loadAllImgs(manager, file, true)
            else if(file.extension() == "pack"){
                manager.load(file.path(), TextureAtlas::class.java)
            }
        }
    }

    fun loadFonts(manager: EasyAssetManager, dir: FileHandle){
        val params = BitmapFontLoader.BitmapFontParameter()
        params.genMipMaps = true
        params.minFilter = Texture.TextureFilter.MipMapLinearLinear
        params.magFilter = Texture.TextureFilter.MipMapLinearLinear

        for(file in dir.list()){
            if(file.extension() == "fnt"){
                manager.load(file.path(), BitmapFont::class.java)
            }
        }
    }

    fun loadMusic(manager: EasyAssetManager, dir: FileHandle){
        for(file in dir.list()){
            if(file.extension() == "ogg"){
                manager.load(file.path(), Music::class.java)
            }
        }
    }
}