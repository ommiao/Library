package cn.ommiao.library.colorpicker

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val DefaultPadding = 16.dp

data class CenterPadding(
    val left: Dp = DefaultPadding,
    val top: Dp = DefaultPadding,
    val right: Dp = DefaultPadding,
    val bottom: Dp = DefaultPadding
) {
    constructor(all: Dp) : this(all, all, all, all)
}
