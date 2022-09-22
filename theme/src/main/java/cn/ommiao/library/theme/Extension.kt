package cn.ommiao.library.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

fun Modifier.clickableWithoutRipple(onClick: () -> Unit) = composed {
    clickable(
        interactionSource = remember {
            MutableInteractionSource()
        },
        indication = null,
        onClick = onClick
    )
}

fun Color.contrastingTextColor(): Color {
    return if (luminance() > 0.6f) Color.Black else Color.White
}
