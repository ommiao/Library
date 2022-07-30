package cn.ommiao.library.colorpicker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class ColorDot(
    val radius: Float = 0f,
    val offset: Offset = Offset.Zero,
    val initialOffset: Offset = Offset.Zero,
    val hsv: Array<Float>,
    val layer: Int = 0
) {

    val color: Color
        get() = Color.hsv(hsv[0], hsv[1], hsv[2])

    val lightnessColor: (lightness: Float) -> Color = {
        Color.hsv(hsv[0], hsv[1], it)
    }

    val distanceTo: (Offset) -> Float = { other ->
        (offset - other).getDistanceSquared()
    }

    private val animatableAlpha = Animatable(ALPHA_START)
    private val animatableOffset =
        Animatable(
            initialValue = initialOffset,
            typeConverter = TwoWayConverter(
                convertToVector = { AnimationVector2D(it.x, it.y) },
                convertFromVector = { Offset(it.v1, it.v2) }
            )
        )

    private var animating = false

    fun getAlpha(): State<Float> {
        return animatableAlpha.asState()
    }

    suspend fun startBlink() {
        animating = true
        alphaTo(ALPHA_END)
    }

    private suspend fun alphaTo(value: Float) {
        animatableAlpha.animateTo(value, animationSpec = tween(600))
        if (animating) {
            alphaTo(if (value == ALPHA_END) ALPHA_START else ALPHA_END)
        } else if (value != ALPHA_START) {
            alphaTo(ALPHA_START)
        }
    }

    fun isBlinking(): Boolean = animatableAlpha.isRunning

    fun stopBlink() {
        animating = false
    }

    fun getOffset(): State<Offset> {
        return animatableOffset.asState()
    }

    suspend fun startOffset() {
        animatableOffset.animateTo(
            targetValue = offset,
            animationSpec = tween(
                600,
                delayMillis = (DENSITY - layer - 1) * 30,
                easing = LinearOutSlowInEasing
            )
        )
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
        private const val ALPHA_START = 1.0f
        private const val ALPHA_END = 0f
    }
}
