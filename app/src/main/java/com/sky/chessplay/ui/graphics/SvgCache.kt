package com.sky.chessplay.ui.graphics

import android.content.Context
import android.graphics.drawable.PictureDrawable
import com.caverock.androidsvg.SVG

object SvgCache {
    private val map = mutableMapOf<String, PictureDrawable>()

    fun get(context: Context, name: String): PictureDrawable {
        return map.getOrPut(name) {
            val svg = SVG.getFromInputStream(context.assets.open(name))
            PictureDrawable(svg.renderToPicture())
        }
    }
}
