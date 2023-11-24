package com.coco.gitcompose.core.util

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.Parcelize

@Parcelize
data class RgbColor(
    val red: Int, val green: Int, val blue: Int
) : Parcelable


fun RgbColor.toColor() = Color(red, green, blue)

fun String.languageToColor(): RgbColor {
    val colors = mutableListOf(1, 1, 1)

    this.forEachIndexed { index, c ->
        val pos = index % 3
        colors[pos] = c.code * colors[pos] % 255
    }

    return RgbColor(colors[0], colors[1], colors[2])
}