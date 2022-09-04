package cn.ommiao.library.colorpicker

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.min

enum class ColorPickerAlignment {
    TopLeft, TopRight, BottomLeft, BottomRight;

    fun getCanvasCenter(size: DpSize, centerPadding: CenterPadding, previewRadius: Dp): DpOffset {
        return when (this) {
            TopLeft -> DpOffset(
                centerPadding.left + previewRadius,
                centerPadding.top + previewRadius
            )
            TopRight -> DpOffset(
                size.width - centerPadding.right - previewRadius,
                centerPadding.top + previewRadius
            )
            BottomLeft -> DpOffset(
                centerPadding.left + previewRadius,
                size.height - centerPadding.bottom - previewRadius
            )
            BottomRight -> DpOffset(
                size.width - centerPadding.right - previewRadius,
                size.height - centerPadding.bottom - previewRadius
            )
        }
    }

    fun getCanvasRadius(
        size: DpSize,
        centerPadding: CenterPadding,
        canvasPadding: Dp,
        previewRadius: Dp
    ): Dp {
        return when (this) {
            TopLeft -> min(size.width - centerPadding.left, size.height - centerPadding.top)
            TopRight -> min(size.width - centerPadding.right, size.height - centerPadding.top)
            BottomLeft -> min(size.width - centerPadding.left, size.height - centerPadding.bottom)
            BottomRight -> min(size.width - centerPadding.right, size.height - centerPadding.bottom)
        } - canvasPadding - previewRadius
    }

    fun getSelectedDotInitialRotation(): Float {
        return when (this) {
            TopLeft -> 315f
            TopRight -> 225f
            BottomLeft -> 45f
            BottomRight -> 135f
        }
    }
}
