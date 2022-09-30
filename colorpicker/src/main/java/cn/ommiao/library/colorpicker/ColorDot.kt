package cn.ommiao.library.colorpicker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class ColorDot(
    val radius: Float = 0f,
    val offset: Offset = Offset.Zero,
    val initialOffset: Offset = Offset.Zero,
    val hsv: Array<Float>,
    val layer: Int = 0
) {
    val diffOffset = offset - initialOffset

    val color: Color
        get() = Color.hsv(hsv[0], hsv[1], hsv[2])

    val lightnessColor: (lightness: Float) -> Color = {
        Color.hsv(hsv[0], hsv[1], it)
    }

    val distanceTo: (Offset) -> Float = { other ->
        (offset - other).getDistanceSquared()
    }

    fun getOffset(progress: Float): Offset {
        return diffOffset * progress + initialOffset
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ColorDot

        if (!hsv.contentEquals(other.hsv)) return false

        return true
    }

    override fun hashCode(): Int {
        return hsv.contentHashCode()
    }

    companion object {
        val Animatables = mutableMapOf<Int, Animatable<Float, AnimationVector1D>>().apply {
            repeat(DENSITY) {
                put(
                    it,
                    Animatable(
                        initialValue = 0f,
                        typeConverter = Float.VectorConverter
                    )
                )
            }
        }
    }
}
